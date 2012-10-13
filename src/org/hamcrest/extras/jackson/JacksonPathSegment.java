package org.hamcrest.extras.jackson;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.hamcrest.extras.Condition.matched;
import static org.hamcrest.extras.Condition.notMatched;

import org.hamcrest.Description;
import org.hamcrest.extras.Condition;
import org.hamcrest.extras.Condition.Step;

import com.fasterxml.jackson.databind.JsonNode;


public class JacksonPathSegment implements Condition.Step<JsonNode, JsonNode> {
        private final String pathSegment;
        private final String pathSoFar;

        public JacksonPathSegment(String pathSegment, String pathSoFar) {
            this.pathSegment = pathSegment;
            this.pathSoFar = pathSoFar;
        }
        @Override
        public Condition<JsonNode> apply(JsonNode current, Description mismatch) {
            if (current.isArray ( )) {
                return nextArrayElement(current, mismatch);
            }
            if ( current.isObject ( )) {
                return nextObject(current, mismatch);
            }
            mismatch.appendText("no value at '").appendText(pathSoFar).appendText("'");
            return notMatched();
        }

        private Condition<JsonNode> nextObject(JsonNode current, Description mismatch) {
            final JsonNode object = current;
            if (!object.has(pathSegment)) {
                mismatch.appendText("missing element at '").appendText(pathSoFar).appendText("'");
                return notMatched();
            }
            return matched(object.get(pathSegment), mismatch);
        }

        private Condition<JsonNode> nextArrayElement(JsonNode current, Description mismatch) {
            final JsonNode array = current; //.getAsJsonArray();
            try {
                return arrayElementIn(array, mismatch);
            } catch (NumberFormatException e) {
                mismatch.appendText("index not a number in ").appendText(pathSoFar);
                return notMatched();
            }
        }

        private Condition<JsonNode> arrayElementIn(JsonNode array, Description mismatch) {
            final int index = parseInt(pathSegment);
            if (index > array.size()) {
                mismatch.appendText(format("index %d too large in ", index)).appendText(pathSoFar);
                return notMatched();
            }
            return matched(array.get(index), mismatch);
        }

        @Override
        public String toString() {
            return "{ segment= " + pathSegment + ", pathSoFar= " + pathSoFar + "}";
        }
}
