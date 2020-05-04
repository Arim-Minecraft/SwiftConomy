/* 
 * SwiftConomy-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * SwiftConomy-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SwiftConomy-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SwiftConomy-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.swiftconomy.core;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

import org.bukkit.OfflinePlayer;

import space.arim.swiftconomy.api.SwiftConomy;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * Core implementation of SwiftConomy which manipulates balances in a thread safe manner. <br>
 * <br>
 * Note that this does not take care of saving / loading balances on startup and shutdown;
 * rather, subclasses must implement {@link #getRawBalance(UUID)} and {@link #getAllUUIDs()}. <br>
 * It is intended that subclasses may decide however which way to store balances, provided that
 * online player balances are stored as <code>AtomicLong</code>.
 * 
 * @author A248
 *
 */
public abstract class AbstractSwiftConomy implements SwiftConomy, UUIDEconomy, UnbankedEconomy, WorldAgnosticEconomy {

	private final DecimalArithmetic arithmetic;
	private final DecimalArithmetic display;
	
	protected AbstractSwiftConomy(int scale, int displayDecimals) {
		ScaleMetrics scaling = Scales.getScaleMetrics(scale);
		arithmetic = scaling.getDefaultArithmetic(); // default == RoundingMode.HALF_UP
		display = arithmetic.deriveArithmetic(displayDecimals);
	}

	/*
	 * 
	 * SwiftConomy implementation
	 * 
	 */
	
	/**
	 * Gets the raw, mutable balance for a player, or <code>null</code>
	 * if the player's account does not exist or is not loaded because the player is offline.
	 * 
	 * @param uuid the uuid of the player
	 * @return the mutable balance or <code>null</code> if not found
	 */
	protected abstract AtomicLong getRawBalance(UUID uuid);
	
	@Override
	public long getBalance(UUID uuid) {
		AtomicLong account = getRawBalance(uuid);
		return (account != null) ? account.get() : -1L;
	}
	
	// left to subclasses
	//public abstract Collection<UUID> getAllUUIDs();

	@Override
	public Boolean deposit(UUID uuid, long deposition) {
		if (deposition < 0) {
			return withdraw(uuid, -deposition);
		}

		AtomicLong balance = getRawBalance(uuid);
		if (balance == null) {
			return false;
		} else if (deposition == 0L) {
			return true;
		}

		getAndAdd(balance, deposition);
		return true;
	}

	@Override
	public Boolean withdraw(UUID uuid, long withdrawal) {
		if (withdrawal < 0) {
			return deposit(uuid, -withdrawal);
		}

		AtomicLong balance = getRawBalance(uuid);
		if (balance == null) {
			return false;
		} else if (withdrawal == 0L) {
			return true;
		}

		long existing;
		long update;
		do {
			existing = balance.get();
			update = existing - withdrawal;
			if (update < 0) {
				// player cannot enter into debt
				return null;
			}
		} while (!compareAndSet(balance, existing, update));

		return true;
	}
	
	@Override
	public Boolean pay(UUID giver, UUID receiver, long transaction) {
		if (transaction < 0) {
			return pay(receiver, giver, -transaction);
		}
		AtomicLong giverBal = getRawBalance(giver);
		AtomicLong receiverBal = getRawBalance(receiver);
		if (giverBal == null || receiverBal == null) {
			return false;
		} else if (transaction == 0L) {
			return true;
		}

		long existing;
		long update;
		do {
			existing = giverBal.get();
			update = existing - transaction;
			if (update < 0) {
				// player cannot enter into debt
				return null;
			}
		} while (!compareAndSet(giverBal, existing, update));
		getAndAdd(receiverBal, transaction);
		return true;
	}

	@Override
	public String displayBalance(long balance) {
		if (balance < 1000L) {
			return "$" + displayBalanceWithoutCurrency(balance);
		} else if (balance > 1000_000_000_000_000_000L) {
			
		}
		int exp = (int) (Math.log10(balance) / 3);
	    return String.format("%.1f %c",
	                         balance / Math.pow(1000, exp),
	                         "KMBTQ".charAt(exp-1));
		//return "$" + displayBalanceWithoutCurrency(balance) + " dollars";
	}

	@Override
	public String displayBalanceWithoutCurrency(long balance) {
		return display.toString(display.fromUnscaled(balance, arithmetic.getScale()));
	}

	@Override
	public DecimalArithmetic getArithmetic() {
		return arithmetic;
	}
	
	/*
	 * Faster versions of AtomicLong methods
	 * See https://dzone.com/articles/wanna-get-faster-wait-bit
	 */
	
	private static boolean compareAndSet(AtomicLong atomLong, long expect, long update) {
		if (!atomLong.compareAndSet(expect, update)) {
			LockSupport.parkNanos(1L);
			return false;
		}
		return true;
	}
	
	private static long getAndAdd(AtomicLong atomLong, long delta) {
        long expected;
        do {
            expected = atomLong.get();
        } while (!compareAndSet(atomLong, expected, expected + delta));
        return expected;
	}
	
	private static long addAndGet(AtomicLong atomLong, long delta) {
		return getAndAdd(atomLong, delta) + delta;
	}
	
	//
	// Vault implementation
	//
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public String getName() {
		return "SwiftConomy";
	}
	
	@Override
	public int fractionalDigits() {
		// We handle the rounding, not just any random plugin
		return -1;
	}
	
	@Override
	public String format(double amount) {
		return displayBalance(arithmetic.fromDouble(amount));
	}
	
	@Override
	public String currencyNamePlural() {
		return "dollars";
	}
	
	@Override
	public String currencyNameSingular() {
		return "dollar";
	}
	
	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return getRawBalance(player.getUniqueId()) != null;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		AtomicLong balance = getRawBalance(player.getUniqueId());
		return (balance != null) ? arithmetic.toDouble(balance.get()) : -1D;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		AtomicLong balance = getRawBalance(player.getUniqueId());
		return (balance != null) && (balance.get() > arithmetic.fromDouble(amount));
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		if (amount < 0) {
			return depositPlayer(player, -amount);
		}

		AtomicLong balance = getRawBalance(player.getUniqueId());
		if (balance == null) {
			return new EconomyResponse(amount, -1D, ResponseType.FAILURE, "Account does not exist for " + player.getUniqueId());
		} else if (amount == 0D) {
			return new EconomyResponse(amount, arithmetic.toDouble(balance.get()), ResponseType.SUCCESS, null);
		}

		long withdrawal = arithmetic.fromDouble(amount);
		long existing;
		long update;
		do {
			existing = balance.get();
			update = existing - withdrawal;
			if (update < 0) {
				// player cannot enter into debt
				return new EconomyResponse(amount, arithmetic.toDouble(existing), ResponseType.FAILURE, null);
			}
		} while (!compareAndSet(balance, existing, update));

		return new EconomyResponse(amount, arithmetic.toDouble(update), ResponseType.SUCCESS, null);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		if (amount < 0) {
			return withdrawPlayer(player, -amount);
		}

		AtomicLong balance = getRawBalance(player.getUniqueId());
		if (balance == null) {
			return new EconomyResponse(amount, -1D, ResponseType.FAILURE, "Account does not exist for " + player.getUniqueId());
		} else if (amount == 0D) {
			return new EconomyResponse(amount, arithmetic.toDouble(balance.get()), ResponseType.SUCCESS, null);
		}

		long update = addAndGet(balance, arithmetic.fromDouble(amount));
		return new EconomyResponse(amount, arithmetic.toDouble(update), ResponseType.SUCCESS, null);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		// We create accounts, not just any random plugin
		return false;
	}
	
	// End Vault implementation

}
