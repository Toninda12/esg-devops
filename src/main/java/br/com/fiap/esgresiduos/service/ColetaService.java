package br.com.fiap.esgresiduos.service;

import br.com.fiap.esgresiduos.dto.ColetaRequest;
import br.com.fiap.esgresiduos.dto.ColetaResponse;
import br.com.fiap.esgresiduos.entity.Coleta;
import br.com.fiap.esgresiduos.entity.PontoColeta;
import br.com.fiap.esgresiduos.entity.Residuo;
import br.com.fiap.esgresiduos.entity.Usuario;
import br.com.fiap.esgresiduos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esgresiduos.exception.RegraNegocioException;
import br.com.fiap.esgresiduos.repository.ColetaRepository;
import br.com.fiap.esgresiduos.repository.ResiduoRepository;
import br.com.fiap.esgresiduos.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColetaService {

    private final ColetaRepository coletaRepository;
    private final PontoColetaService pontoColetaService;
    private final UsuarioRepository usuarioRepository;
    private final ResiduoRepository residuoRepository;
    private final AlertaService alertaService;

    @Transactional(readOnly = true)
    public List<ColetaResponse> listarTodas() {
        return coletaRepository.findAll()
                .stream()
                .map(ColetaResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ColetaResponse> listarPorPontoColeta(Long idPontoColeta) {
        if (idPontoColeta == null || idPontoColeta <= 0) {
            throw new RegraNegocioException(
                    "O ID do ponto de coleta deve ser positivo."
            );
        }

        return coletaRepository.findByPontoColetaId(idPontoColeta)
                .stream()
                .map(ColetaResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ColetaResponse buscarPorId(Long id) {
        return ColetaResponse.fromEntity(buscarEntidadePorId(id));
    }

    @Transactional
    public ColetaResponse registrar(ColetaRequest request) {
        if (request == null) {
            throw new RegraNegocioException(
                    "Os dados da coleta são obrigatórios."
            );
        }

        validarQuantidade(request.quantidadeKg());

        if (request.tipoOperacao() == null) {
            throw new RegraNegocioException(
                    "O tipo de operação é obrigatório."
            );
        }

        PontoColeta ponto = pontoColetaService
                .buscarEntidadePorId(request.idPontoColeta());

        Usuario usuario = buscarUsuarioPorId(request.idUsuario());

        Residuo residuo = request.idResiduo() != null
                ? buscarResiduoPorId(request.idResiduo())
                : null;

        if (request.tipoOperacao() == Coleta.TipoOperacao.DESCARTE) {
            validarDescarteNoPonto(ponto, request.quantidadeKg());
        }

        Coleta coleta = Coleta.builder()
                .pontoColeta(ponto)
                .usuario(usuario)
                .residuo(residuo)
                .quantidadeKg(request.quantidadeKg())
                .observacao(request.observacao())
                .tipoOperacao(request.tipoOperacao())
                .build();

        Coleta coletaPersistida = coletaRepository.save(coleta);

        atualizarVolumeDoPonto(
                ponto,
                request.quantidadeKg(),
                request.tipoOperacao()
        );

        log.info(
                "Coleta registrada: ID={}, ponto={}, operação={}, quantidade={} kg",
                coletaPersistida.getId(),
                ponto.getNome(),
                request.tipoOperacao(),
                request.quantidadeKg()
        );

        return ColetaResponse.fromEntity(coletaPersistida);
    }

    private void validarQuantidade(BigDecimal quantidadeKg) {
        if (quantidadeKg == null
                || quantidadeKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(
                    "A quantidade deve ser maior que zero."
            );
        }
    }

    private void validarDescarteNoPonto(
            PontoColeta ponto,
            BigDecimal quantidadeKg) {

        if (ponto.getStatus() != PontoColeta.StatusPontoColeta.ATIVO) {
            throw new RegraNegocioException(
                    "O ponto de coleta não está ativo."
            );
        }

        BigDecimal capacidade = ponto.getCapacidadeMaximaKg();
        BigDecimal volumeAtual = ponto.getVolumeAtualKg();

        if (capacidade == null || volumeAtual == null) {
            throw new RegraNegocioException(
                    "A capacidade e o volume atual do ponto devem estar definidos."
            );
        }

        if (capacidade.compareTo(BigDecimal.ZERO) <= 0
                || volumeAtual.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException(
                    "A capacidade ou o volume atual do ponto é inválido."
            );
        }

        BigDecimal capacidadeRestante = capacidade.subtract(volumeAtual);

        if (quantidadeKg.compareTo(capacidadeRestante) > 0) {
            throw new RegraNegocioException(
                    "A quantidade informada excede a capacidade restante."
            );
        }
    }

    private void atualizarVolumeDoPonto(
            PontoColeta ponto,
            BigDecimal quantidadeKg,
            Coleta.TipoOperacao tipoOperacao) {

        BigDecimal volumeAtual = ponto.getVolumeAtualKg();

        if (volumeAtual == null) {
            throw new RegraNegocioException(
                    "O volume atual do ponto não está definido."
            );
        }

        if (tipoOperacao == Coleta.TipoOperacao.DESCARTE) {
            BigDecimal novoVolume = volumeAtual.add(quantidadeKg);
            ponto.setVolumeAtualKg(novoVolume);

            if (novoVolume.compareTo(ponto.getCapacidadeMaximaKg()) >= 0) {
                ponto.setStatus(PontoColeta.StatusPontoColeta.CHEIO);
            }

            alertaService.verificarEGerarAlerta(ponto);

        } else if (tipoOperacao == Coleta.TipoOperacao.RETIRADA) {
            BigDecimal novoVolume = volumeAtual
                    .subtract(quantidadeKg)
                    .max(BigDecimal.ZERO);

            ponto.setVolumeAtualKg(novoVolume);
            ponto.setStatus(PontoColeta.StatusPontoColeta.ATIVO);
        }
    }

    private Coleta buscarEntidadePorId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraNegocioException(
                    "O ID da coleta deve ser positivo."
            );
        }

        return coletaRepository.findById(id)
                .orElseThrow(
                        () -> new EntidadeNaoEncontradaException("Coleta", id)
                );
    }

    private Usuario buscarUsuarioPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraNegocioException(
                    "O ID do usuário deve ser positivo."
            );
        }

        return usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new EntidadeNaoEncontradaException("Usuário", id)
                );
    }

    private Residuo buscarResiduoPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraNegocioException(
                    "O ID do resíduo deve ser positivo."
            );
        }

        return residuoRepository.findById(id)
                .orElseThrow(
                        () -> new EntidadeNaoEncontradaException("Resíduo", id)
                );
    }
}