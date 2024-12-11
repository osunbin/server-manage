package com.bin.sm;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

public class ElementVisitor8 extends SimpleElementVisitor8<TypeElement, TypeElement> {
    private final BeanUtils utils;
    public ElementVisitor8(BeanUtils utils) {
        this.utils = utils;
    }
    @Override
    public TypeElement visitPackage(PackageElement packageElement, TypeElement p) {
        return null;
    }

    @Override
    public TypeElement visitType(TypeElement enclosingClass, TypeElement p) {
        if (utils.isAssignable(enclosingClass, p)) {
            return enclosingClass;
        }
        return null;
    }

    @Override
    public TypeElement visitUnknown(Element unknown, TypeElement p) {
        return null;
    }

    @Override
    public TypeElement defaultAction(Element enclosingElement, TypeElement p) {
        throw new IllegalArgumentException("Unexpected type nesting: " + p);
    }
}
