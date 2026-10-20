package br.com.faculdadedonaduzzi.dataservice.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;


@Tag(name = "Classificação Controller Peixes", description = "Controller responsável por processar requisições de classificação de spam")
@RestController()
@RequestMapping("api/classificao")
public class ClassificacaoController {

    private final String PYTHON_INTERPRETER = "python3";
    private final String SCRIPT_PATH = "/app/scripts/predict.py";

    @PostMapping("classificar-peixes")
    @ApiResponse(description = "Sucesso", responseCode = "202")
    @ApiResponse(description = "Erro durante o processamento no Python", responseCode = "500")
    public ResponseEntity<String> classificar(
            @RequestParam("imagem") MultipartFile imagemSerClassificadaPeloPython) {

        Path path = null;
            try {
                //criando o arquivo temporário
                path = Files.createTempFile("fish_", ".jpg");
                //transferindo o arquivo da imagem para o novo arquivo temporário criado
                imagemSerClassificadaPeloPython.transferTo(path.toFile());
                //criando o processo oculto do python que irá chamar o interpretador
                ProcessBuilder pb = new ProcessBuilder(
                        // python predict.py "caminho da imagem a ser processada criada na temp"
                        PYTHON_INTERPRETER, SCRIPT_PATH, path.toString()
                );
                //unificando o stdout e o stderr
                pb.redirectErrorStream(true);
                // agora sim VAAAAAAAAI
                Process p = pb.start();
                //Abri um buffer read de um inputstreamReader do meu processo
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                // apendeia no stringBuilder
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                int codigoSaida = p.waitFor();
                // higiene do servidore senão ZERO SSD in the future
                Files.deleteIfExists(path);

                if(codigoSaida != 0){
                    return ResponseEntity.status(500).body("Erro no script python" + sb.toString());
                }
                String resultado = sb.toString().trim();
                if (resultado.equals("FRESCO") || resultado.equals("ESTRAGADO")) {
                    return ResponseEntity.ok().body(resultado);
                }else{
                    return ResponseEntity.status(500).body("Erro inesperada no script python" + resultado);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        return ResponseEntity.status(500).body("Erro no script python");
    }
}
