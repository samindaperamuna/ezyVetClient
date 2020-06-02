package org.fifthgen.evervet.ezyvet.api;

import org.fifthgen.evervet.ezyvet.api.callback.GetAnimalsListCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetTokenCallback;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.model.Token;
import org.fifthgen.evervet.ezyvet.api.model.TokenScope;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

public class APIV1Test {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private APIV1 api;

    @Before
    public void setUp() {
        this.api = new APIV1();
    }

    @Test
    public void getAccessTokenTest() {
        api.getAccessToken(TokenScope.READ_ANIMAL, new GetTokenCallback() {
            @Override
            public void onCompleted(Token token) {
                Assert.assertNotNull(token);
                System.out.println(token.toString());
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                e.printStackTrace();
            }
        });
    }

    @Test
    public void getAnimalsListTest() {
        api.getAnimalsList(new GetAnimalsListCallback() {
            @Override
            public void onCompleted(List<Animal> animalList) {
                Assert.assertNotNull(animalList);
                System.out.println(Arrays.toString(animalList.toArray()));
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                e.printStackTrace();
            }
        });
    }
}
