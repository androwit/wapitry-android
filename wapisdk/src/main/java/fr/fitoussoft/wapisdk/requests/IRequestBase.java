package fr.fitoussoft.wapisdk.requests;

/**
 * Created by emmanuel.fitoussi on 30/11/2014.
 */
public interface IRequestBase<T> {
    T execute();

    boolean isWithAccessToken();
}
