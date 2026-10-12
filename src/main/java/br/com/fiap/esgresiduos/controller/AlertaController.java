package br.com.fiap.esgresiduos.controller;

import br.com.fiap.esgresiduos.dto.AlertaResponse;
import br.com.fiap.esgresiduos.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para consulta e gestão dos Alertas automáticos.
 * <p>
 * Endpoints expostos:
 * <ul>
 *   <li>GET   /alertas                          — Lista todos os alertas</li>
 *   <li>GET   /alertas?status=PENDENTE          — Filtra por status</li>
 *   <li>GET   /alertas?idPontoColeta=           — Filtra por ponto</li>
 *   <li>PATCH /alertas/{id}/resolver            — Marca alerta como resolvido</li>
 * </ul>
 */
@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    // ── GET /alertas ──────────────────────────────────────────────────────────

    /**
     * Lista alertas com filtros opcionais por status ou ponto de coleta.
     * Se nenhum filtro for informado, retorna todos os alertas.
     *
     * @param pendentesApenas Se {@code true}, retorna apenas alertas PENDENTES.
     * @param idPontoColeta   (opcional) Filtra alertas de um ponto específico.
     * @return 200 OK com a lista de alertas.
     */
    @GetMapping
    public ResponseEntity<List<AlertaResponse>> listar(
            @RequestParam(name = "pendentes", required = false, defaultValue = "false")
                boolean pendentesApenas,
            @RequestParam(required = false)
                Long idPontoColeta) {

        List<AlertaResponse> alertas;

        if (idPontoColeta != null) {
            alertas = alertaService.listarPorPontoColeta(idPontoColeta);
        } else if (pendentesApenas) {
            alertas = alertaService.listarPendentes();
        } else {
            alertas = alertaService.listarTodos();
        }

        return ResponseEntity.ok(alertas);
    }

    // ── PATCH /alertas/{id}/resolver ──────────────────────────────────────────

    /**
     * Marca um alerta como RESOLVIDO, registrando a data de resolução.
     * Utilizado quando o ponto de coleta foi esvaziado ou atendido.
     *
     * @param id ID do alerta a ser resolvido.
     * @return 200 OK com os dados do alerta atualizado, ou 404 Not Found.
     */
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<AlertaResponse> resolver(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.resolverAlerta(id));
    }
}
