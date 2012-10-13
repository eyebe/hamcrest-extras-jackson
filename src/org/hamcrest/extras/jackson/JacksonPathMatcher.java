package org.hamcrest.extras.jackson;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.extras.Condition.matched;
import java.util.ArrayList;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.extras.Condition;
import org.hamcrest.extras.Condition.Step;

import com.fasterxml.jackson.databind.JsonNode;


public class JacksonPathMatcher extends TypeSafeDiagnosingMatcher<JsonNode> {
    private final String jsonPath;
    private Condition.Step<? super JsonNode, JsonNode> findElement;
    private Matcher<JsonNode> elementContents;

    public JacksonPathMatcher(String jsonPath, Matcher<JsonNode> elementContents) {
        this.jsonPath = jsonPath;
        this.findElement = findElementStep(jsonPath);
        this.elementContents = elementContents;
    }

    @Override
    protected boolean matchesSafely(JsonNode root, Description mismatch ) {
        return matched ( root, mismatch).and ( findElement ).matching ( elementContents );
    }


    @Override
    public void describeTo(Description description) {
       description.appendText("Json with path '").appendText(jsonPath).appendText("'")
                  .appendDescriptionOf(elementContents);
    }

    @Factory
    public static Matcher<JsonNode> hasJsonPath(final String jsonPath) {
        return new JacksonPathMatcher(jsonPath, any(JsonNode.class));
    }

    @Factory
    public static Matcher<JsonNode> hasJsonElement(final String jsonPath, final Matcher<JsonNode> contentsMatcher) {
        return new JacksonPathMatcher(jsonPath, elementWith(contentsMatcher));
    }

    private static Matcher<JsonNode> elementWith(final Matcher<JsonNode> contentsMatcher) {
        return new TypeSafeDiagnosingMatcher<JsonNode>() {
            @Override
            protected boolean matchesSafely(JsonNode element, Description mismatch) {
                return jsonPrimitive(element, mismatch).matching(contentsMatcher);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("containing ").appendDescriptionOf(contentsMatcher);
            }

            private Condition<JsonNode> jsonPrimitive(JsonNode element, Description mismatch) {
                if ((element.isObject ( ) || element.isArray ( ))) {
                    return Condition.<JsonNode>matched(element, mismatch);
                }
                mismatch.appendText("element was ").appendValue(element);
                return Condition.<JsonNode>notMatched();
            }
        };
    }

    private static Condition.Step<JsonNode, JsonNode> findElementStep(final String jsonPath) {
        return new Condition.Step<JsonNode, JsonNode>() {
            @Override
            public Condition<JsonNode> apply(JsonNode root, Description mismatch) {
                Condition<JsonNode> current = matched(root, mismatch);
                for (JacksonPathSegment nextSegment : split(jsonPath)) {
                    current = current.then(nextSegment);
                }
                return current;
            }
        };
    }

    private static Iterable<JacksonPathSegment> split(String jsonPath) {
        final ArrayList<JacksonPathSegment> segments = new ArrayList<JacksonPathSegment>();
        final StringBuilder pathSoFar = new StringBuilder();
        for (String pathSegment : jsonPath.split("\\.")) {
            pathSoFar.append(pathSegment);
            final int leftBracket = pathSegment.indexOf('[');
            if (leftBracket == -1) {
                segments.add(new JacksonPathSegment(pathSegment, pathSoFar.toString()));
            } else {
                segments.add(new JacksonPathSegment(pathSegment.substring(0, leftBracket), pathSoFar.toString()));
                segments.add(new JacksonPathSegment(pathSegment.substring(
                        leftBracket + 1, pathSegment.length() - 1), pathSoFar.toString()));
            }
            pathSoFar.append(".");
        }
        return segments;
    }

    @Override
    public String toString () {
        return "{ path= " + jsonPath + ", findElement= " + findElement  + ", contents= " + elementContents + "}";
    }
}
