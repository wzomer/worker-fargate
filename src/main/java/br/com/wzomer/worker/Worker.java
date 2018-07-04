package br.com.wzomer.worker;

import com.amazonaws.auth.ContainerCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import groovy.lang.GroovyShell;

import java.io.IOException;

public class Worker {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static void main(String[] args) throws IOException {
        final String urlSolicitacao = System.getProperty("urlSolicitacao");


        if (urlSolicitacao == null || urlSolicitacao.isEmpty()) {
            throw new IllegalArgumentException("É necessário informar a url da solicitação.");
        }

        final HttpRequestFactory requestFactory
                = HTTP_TRANSPORT.createRequestFactory(
                (HttpRequest request) -> {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                });

        final SolicitacaoDto solicitacaoDto = requestFactory
                .buildGetRequest(new GenericUrl(urlSolicitacao))
                .execute()
                .parseAs(SolicitacaoDto.class);

        final GroovyShell shell = new GroovyShell();
        final Object value = shell.evaluate(solicitacaoDto.getScript());

        System.out.println("Retorno do script: " + value);

        final String nomeArquivo = "resultado" + urlSolicitacao.substring(urlSolicitacao.lastIndexOf("/")).replace(".json", ".txt");

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new ContainerCredentialsProvider())
                .withRegion(Regions.US_EAST_1).build();
        s3Client.putObject("wzomer-worker", nomeArquivo, value.toString());
    }

}
