/* 
 * SwiftConomy-api
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * SwiftConomy-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SwiftConomy-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SwiftConomy-api. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.swiftconomy.api;

import java.util.Collection;
import java.util.UUID;

import org.decimal4j.api.DecimalArithmetic;

/**
 * SwiftConomy official API <br>
 * <br>
 * SwiftConomy is designed to use decimal4j's zero garbage computations: <br>
 * <a href=https://github.com/tools4j/decimal4j/wiki/DecimalArithmetic-API> <br>
 * <br>
 * Once it is understood how decimal4j works, how SwiftConomy works becomes clear. <br>
 * <br>
 * The balance of each player is stored internally as a long (really an AtomicLong for concurrency purposes).
 * This such balance may be referred to as the "raw balance" or the "internal balance".
 * 
 * @author A248
 *
 */
public interface SwiftConomy {

	/**
	 * Gets the internal balance for the player. <br>
	 * If no balance is stored corresponding to the uuid,
	 * <code>{@literal -}1</code> is returned.
	 * 
	 * @param uuid the uuid of the player
	 * @return the raw balance or <code>null</code> if not found
	 */
	long getBalance(UUID uuid);
	
	/**
	 * Gets all uuids for which balances are stored. <br>
	 * The returned collection is a copy representing the state of SwiftConomy at call time. <br>
	 * <br>
	 * If you only need to determine whether a balance is stored for a specific UUID,
	 * this method is not advised. Instead, simply use <code>getRawBalance(uuid) != null</code>.
	 * 
	 * @return a copy of all uuids for which balances are stored, may or may not be mutable
	 */
	Collection<UUID> getAllUUIDs();
	
	/**
	 * Deposits the given amount on behalf of the player. <br>
	 * <br>
	 * The amount should be positive. However, if the specified amount is negative,
	 * the result of {@link #withdraw(UUID, long)} using a positive amount is returned,
	 * in which case this may return <code>null</code>. <br>
	 * <br>
	 * If there is no balance stored corresponding to the uuid,
	 * nothing happens, <code>false</code> is returned. <br>
	 * Otherwise, the balance is added to the player's account
	 * and <code>true</code> is returned.
	 * 
	 * @param uuid the uuid of the player
	 * @param deposition the amount to deposit, an unscaled long (see decimal4j)
	 * @return true if the player had a balance, false otherwise
	 */
	Boolean deposit(UUID uuid, long deposition);
	
	/**
	 * Withdraws the given amount on behalf of the player. <br>
	 * <br>
	 * The amount should be positive. However, if the specified amount is negative,
	 * the result of {@link #deposit(UUID, long)} using a positive amount is returned. <br>
	 * <br>
	 * If there is no balance stored corresponding to the uuid,
	 * nothing happens, <code>false</code> is returned. <br>
	 * If the withdrawal cannot take place because it would otherwise
	 * force the player to enter into debt, <code>null</code> is returned. <br>
	 * Otherwise, the balance is subtracted from the player's account
	 * and <code>true</code> is returned.
	 * 
	 * @param uuid the uuid of the player
	 * @param withdrawal the amount to withdraw, an unscaled long (see decimal4j)
	 * @return a tristate indicating success (true), failed withdrawal (null), or no account (false)
	 */
	Boolean withdraw(UUID uuid, long withdrawal);
	
	/**
	 * Formats the internal balance of a user including currency units. <br>
	 * For example, this might display "$15.00 dollars"
	 * 
	 * @param balance the raw balance
	 * @return the formatted display
	 */
	String displayBalanceWithCurrency(long balance);
	
	/**
	 * Formats the internal balance of a user as a simple number. <br>
	 * For example, this might display "15.00".
	 * 
	 * @param balance the raw balance
	 * @return the formatted display
	 */
	String displayBalance(long balance);
	
	/**
	 * Gets the decimal4j arithmetic which SwiftConomy uses.
	 * 
	 * @return the arithmetic
	 */
	DecimalArithmetic getArithmetic();
	
}
