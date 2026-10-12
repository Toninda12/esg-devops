package br.com.fiap.esgresiduos.controller;

import br.com.fiap.esgresiduos.dto.ColetaRequest;
import br.com.fiap.esgresiduos.dto.ColetaResponse;
import br.com.fiap.esgresiduos.service.ColetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para operações sobre coletas de resíduos.
 *
 * Endpoints:
 * GET  /coletas
 * GET  /coletas/{id}
 * POST /coletas
 */
@RestController
@RequestMapping("/coletas")
public class ColetaController {

    private final ColetaService coletaService;

    /**
     * Injeta o serviço responsável pelas operações de coleta.
     */
    public ColetaController(ColetaService coletaService) {
        this.coletaService = coletaService;
    }

    /**
     * Lista todas as coletas ou filtra por ponto de coleta.
     */
    @GetMapping
    public ResponseEntity<List<ColetaResponse>> listar(
            @RequestParam(required = false) Long idPontoColeta) {

        List<ColetaResponse> coletas;

        if (idPontoColeta != null) {
            if (idPontoColeta <= 0) {
                return ResponseEntity.badRequest().build();
            }

            coletas = coletaService.listarPorPontoColeta(idPontoColeta);
        } else {
            coletas = coletaService.listarTodas();
        }

        return ResponseEntity.ok(coletas);
    }

    /**
     * Busca uma coleta pelo identificador.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ColetaResponse> buscarPorId(
            @PathVariable Long id) {

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        ColetaResponse coleta = coletaService.buscarPorId(id);

        return ResponseEntity.ok(coleta);
    }

    /**
     * Registra uma nova coleta.
     */
    @PostMapping
    public ResponseEntity<ColetaResponse> registrar(
            @Valid @RequestBody ColetaRequest request) {

        ColetaResponse coletaRegistrada = coletaService.registrar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(coletaRegistrada.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(coletaRegistrada);
    }
}
