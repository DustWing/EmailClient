package org.emailclient.policies;

public interface IValidatePolicy<T> {


    boolean validate(T t);

    boolean throwException();

}
