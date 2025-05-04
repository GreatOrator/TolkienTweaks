package com.greatorator.tolkienmobs.handler.vec;

import com.greatorator.tolkienmobs.util.Copyable;
import org.jetbrains.annotations.Nullable;

public abstract class ITransformation<Vector, Transformation extends ITransformation<Vector, Transformation>> implements Copyable<Transformation> {

    public abstract void apply(Vector vec);

    public abstract Transformation at(Vector point);

    public abstract Transformation with(Transformation t);

    @Nullable
    public Transformation merge(Transformation next) {
        return null;
    }

    public boolean isRedundant() {
        return false;
    }

    public abstract Transformation inverse() throws IrreversibleTransformationException;

    public Transformation $plus$plus(Transformation t) {
        return with(t);
    }
}
