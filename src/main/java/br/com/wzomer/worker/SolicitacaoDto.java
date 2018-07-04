package br.com.wzomer.worker;

import com.google.api.client.util.Key;

public class SolicitacaoDto {

    @Key
    private String script;

    String getScript() {
        return script;
    }
}
