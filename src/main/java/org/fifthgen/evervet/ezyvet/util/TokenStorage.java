package org.fifthgen.evervet.ezyvet.util;

import org.fifthgen.evervet.ezyvet.api.model.Token;
import org.fifthgen.evervet.ezyvet.api.model.TokenScope;

/**
 * Used to store a single token as as it is fetched from the API and retrieve it later.
 * Holds the token in session memory.
 * Make sure that the token obtained from this store is validated before use
 * as it might be expired at the time of use.
 */
public class TokenStorage {

    // Singleton instance variable.
    private static TokenStorage instance;

    // Holds the current token scope.
    private TokenScope scope;

    // Holds the current token instance.
    private Token token;

    /**
     * Private constructor ensures the instantiation of this class remains singleton.
     */
    private TokenStorage() {
        this.token = null;
        this.scope = null;
    }

    /**
     * Get the singleton instance of the class. Initiate first if null.
     *
     * @return Singleton instance of the class.
     */
    public static TokenStorage getInstance() {
        if (instance == null) {
            instance = new TokenStorage();
        }

        return instance;
    }

    /**
     * Store the given token in token store, under the given scope as key.
     *
     * @param scope Scope, to store the token in, as key.
     * @param token Token to be stored.
     */
    public void storeToken(TokenScope scope, Token token) {
        this.token = token;
        this.scope = scope;
    }

    /**
     * Get a specific token from the token store.
     *
     * @param scope Scope of the token to be retrieved.
     * @return Token if found, else null.
     */
    public Token getToken(TokenScope scope) {
        if (this.scope == scope) {
            return token;
        } else {
            return null;
        }
    }

    /**
     * Check whether the token store has a saved token with the scope.
     *
     * @param scope Scope of the token.
     * @return Whether the store contains specific scope as key.
     */
    public boolean hasToken(TokenScope scope) {
        return this.scope == scope;
    }
}
