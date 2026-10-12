package br.com.fiap.esgresiduos.controller;

import br.com.fiap.esgresiduos.dto.AtualizacaoCapacidadeRequest;
import br.com.fiap.esgresiduos.dto.PontoColetaRequest;
import br.com.fiap.esgresiduos.dto.PontoColetaResponse;
import br.com.fiap.esgresiduos.entity.PontoColeta;
import br.com.fiap.esgresiduos.service.PontoColetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para operações sobre Pontos de Coleta.
 * <p>
 * Endpoints expostos:
 * <ul>
 *   <li>GET    /pontos-coleta             — Lista todos os pontos</li>
 *   <li>GET    /pontos-coleta/{id}        — Busca ponto por ID</li>
 *   <li>GET    /pontos-coleta?tipo=       — Filtra por tipo de resíduo</li>
 *   <li>POST   /pontos-coleta             — Cria novo ponto</li>
 *   <li>PUT    /pontos-coleta/{id}        — Atualiza dados cadastrais</li>
 *   <li>PUT    /pontos-coleta/{id}/capacidade — Atualiza volume atual</li>
 *   <li>DELETE /pontos-coleta/{id}        — Remove ponto</li>
 * </ul>
 * <p>
 * Todos os endpoints estão prontos para serem protegidos pelo
 * Spring Security (Pessoa 3 — configuração de segurança).
 */
@RestController
@RequestMapping("/pontos-coleta")
@RequiredArgsConstructor
public class PontoColetaController {

    private final PontoColetaService pontoColetaService;

    // ── GET /pontos-coleta ────────────────────────────────────────────────────

    /**
     * Lista todos os pontos de coleta.
     * Aceita filtro opcional por tipo de resíduo via query param.
     *
     * @param tipo (opcional) Tipo de resíduo para filtrar.
     * @return 200 OK com a lista de pontos.
     */
    @GetMapping
    public ResponseEntity<List<PontoColetaResponse>> listar(
            @RequestParam(required = false) PontoColeta.TipoResiduo tipo) {

        List<PontoColetaResponse> pontos = tipo != null
            ? pontoColetaService.listarPorTipoResiduo(tipo)
            : pontoColetaService.listarTodos();

        return ResponseEntity.ok(pontos);
    }

    // ── GET /pontos-coleta/{id} ───────────────────────────────────────────────

    /**
     * Busca um ponto de coleta pelo ID.
     *
     * @param id ID do ponto.
     * @return 200 OK com os dados do ponto, ou 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pontoColetaService.buscarPorId(id));
    }

    // ── POST /pontos-coleta ───────────────────────────────────────────────────

    /**
     * Cadastra um novo ponto de coleta.
     *
     * @param request Dados do ponto a ser criado (validado pelo Bean Validation).
     * @return 201 Created com o ponto criado e header Location.
     */
    @PostMapping
    public ResponseEntity<PontoColetaResponse> criar(
            @Valid @RequestBody PontoColetaRequest request) {

        PontoColetaResponse pontoCreated = pontoColetaService.criar(request);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(pontoCreated.id())
            .toUri();

        return ResponseEntity.created(location).body(pontoCreated);
    }

    // ── PUT /pontos-coleta/{id} ───────────────────────────────────────────────

    /**
     * Atualiza os dados cadastrais de um ponto de coleta.
     *
     * @param id      ID do ponto a ser atualizado.
     * @param request Novos dados (validado pelo Bean Validation).
     * @return 200 OK com os dados atualizados, ou 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PontoColetaRequest request) {

        return ResponseEntity.ok(pontoColetaService.atualizar(id, request));
    }

    // ── PUT /pontos-coleta/{id}/capacidade ────────────────────────────────────

    /**
     * Atualiza o volume atual (em kg) de um ponto de coleta.
     * <p>
     * Após a atualização, o sistema verifica automaticamente se o limite
     * de capacidade foi atingido e gera um alerta, se necessário.
     *
     * @param id      ID do ponto.
     * @param request Novo volume em quilogramas.
     * @return 200 OK com os dados atualizados, incluindo o novo percentual de ocupação.
     */
    @PutMapping("/{id}/capacidade")
    public ResponseEntity<PontoColetaResponse> atualizarCapacidade(
            @PathVariable Long id,
            @Valid @RequestBody AtualizacaoCapacidadeRequest request) {

        return ResponseEntity.ok(pontoColetaService.atualizarCapacidade(id, request));
    }

    // ── DELETE /pontos-coleta/{id} ────────────────────────────────────────────

    /**
     * Remove um ponto de coleta pelo ID.
     * Só é permitido remover pontos sem coletas associadas.
     *
     * @param id ID do ponto a ser removido.
     * @return 204 No Content em caso de sucesso, ou 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pontoColetaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
