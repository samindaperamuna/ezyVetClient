package org.fifthgen.evervet.ezyvet.util;

import org.fifthgen.evervet.ezyvet.api.model.Token;
import org.fifthgen.evervet.ezyvet.api.model.TokenScope;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to store tokens as they are fetched from the API and retrieve existing tokens.
 * Holds tokens in session memory.
 * Make sure that the tokens obtained from this store are validated before use
 * as the might be expired at the time of use.
 */
public class TokenStorage {

    // Singleton instance variable.
    private static TokenStorage instance;

    // Token storage.
    private final Map<TokenScope, Token> tokenStore;

    /**
     * Private constructor ensures the instantiation of this class remains singleton.
     */
    private TokenStorage() {
        this.tokenStore = new HashMap<>();
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
        this.tokenStore.put(scope, token);
    }

    /**
     * Get a specific token from the token store.
     *
     * @param scope Scope of the token to be retrieved.
     * @return Token if found, else null.
     */
    public Token getToken(TokenScope scope) {
        return this.tokenStore.get(scope);
    }

    /**
     * Check whether the token store has a saved token with the scope.
     *
     * @param scope Scope of the token.
     * @return Whether the store contains specific scope as key.
     */
    public boolean hasToken(TokenScope scope) {
        return this.tokenStore.containsKey(scope);
    }
}
