package org.tastefuljava.simuli.render;

import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.model.Patch;

public class Grabbers {
    public static final HitTester<Output> OUTPUT_GRABBER
            = new HitTester<Output>() {
        @Override
        public Output outputName(Patch patch, Output out) {
            return out;
        }

        @Override
        public Output outputPin(Patch patch, Output out) {
            return out;
        }
    };

    public static final HitTester<Input> INPUT_GRABBER
            = new HitTester<Input>() {
        @Override
        public Input inputName(Patch patch, Input in) {
            return in;
        }

        @Override
        public Input inputPin(Patch patch, Input in) {
            return in;
        }
     };
}
