package com.palmergames.bukkit.TownyChat.util;

/**
 * A Callable used for regex matching.
 * Used in conjunction with StringReplaceManager.
 * 
 * @author Chris H (Shade)
 */
public interface ReplacerCallable<E> {
	public String call(String match, E e) throws Exception;
}
