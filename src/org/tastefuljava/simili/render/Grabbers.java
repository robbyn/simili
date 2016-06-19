package org.tastefuljava.simili.render;

import org.tastefuljava.simili.model.Input;
import org.tastefuljava.simili.model.Output;
import org.tastefuljava.simili.model.Patch;

public class Grabbers {
    public static final HitTester<Output> OUTPUT_GRABBER
            = new AbstractHitTester<Output>() {
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
            = new AbstractHitTester<Input>() {
        @Override
        public Input inputName(Patch patch, Input in) {
            return in;
        }

        @Override
        public Input inputPin(Patch patch, Input in) {
            return in;
        }
     };

    public static abstract class AbstractHitTester<T> implements HitTester<T> {

        @Override
        public T patchTitle(Patch patch) {
            return null;
        }

        @Override
        public T patch(Patch patch) {
            return null;
        }

        @Override
        public T inputPin(Patch patch, Input in) {
            return null;
        }

        @Override
        public T inputName(Patch patch, Input in) {
            return null;
        }

        @Override
        public T outputPin(Patch patch, Output out) {
            return null;
        }

        @Override
        public T outputName(Patch patch, Output out) {
            return null;
        }

        @Override
        public T background() {
            return null;
        }
    }
}
