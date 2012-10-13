package org.hamcrest.extras.jackson;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.fasterxml.jackson.databind.JsonNode;

public class IsNull extends TypeSafeDiagnosingMatcher<JsonNode> {
    public static Matcher<JsonNode> hasNullValue () {
        return new IsNull ( );
    }

    @Override
    public void describeTo ( Description description ) {
        if ( description != null ) {
            description.appendText ( "null value" );
        }
    }

    @Override
    protected boolean matchesSafely ( JsonNode item, Description mismatchDescription ) {
        if ( ! item.isNull ( ) ) {
            describeTo ( mismatchDescription );
            return false;
        }
        return true;
    }

}
