package org.hamcrest.extras.jackson;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Tests if {@link JsonNode} instance is empty -- as in whether or
 * not a container node is not empty.
 * <br/>
 *  Always returns "false" if node is "missing"
 *
 */
public class IsEmptyNode extends TypeSafeDiagnosingMatcher<JsonNode> {

    public static Matcher<JsonNode> isEmpty() {
        return new IsEmptyNode ( );
    }

    @Override
    public void describeTo ( Description description ) {
        if ( description != null ) {
            description.appendText ( "empty " );
        }
    }

    @Override
    protected boolean matchesSafely ( JsonNode item, Description mismatchDescription ) {
        if ( item.isMissingNode ( ) ) {
            return false;
        }
        // object or array
        if ( item.isContainerNode ( ) ) {
            return item.size ( ) == 0;
        }
        // primitive value
        // a node with no value ( aka "null value") is empty
        return item.isNull ( );
    }
}
