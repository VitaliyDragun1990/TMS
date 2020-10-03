package org.vdragun.tms.util.generator;

/**
 * Signals about errors in data generation process
 * 
 * @author Vitaliy Dragun
 *
 */
public class GenerationException extends RuntimeException {
    private static final long serialVersionUID = 8862322616394103892L;

    public GenerationException(String message) {
        super(message);
    }

}
