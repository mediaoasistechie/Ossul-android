package com.ossul.interfaces;

/**
 * @author Rajan Tiwari on 13-Dec-16
 */
public interface IValidationResult {

    void onValidationError(int errorType, int errorResId);

    void onValidationSuccess();
}
