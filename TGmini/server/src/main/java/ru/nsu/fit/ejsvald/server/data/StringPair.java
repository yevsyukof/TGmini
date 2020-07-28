package ru.nsu.fit.ejsvald.server.data;

public class StringPair { //FIXME: надеемся он так умеет преобразовывать в json

    public final String request;
    public final int idToPutAnswer;

    public StringPair(String curRequest, int freeKey) {
        request = curRequest;
        idToPutAnswer = freeKey;
    }

}
