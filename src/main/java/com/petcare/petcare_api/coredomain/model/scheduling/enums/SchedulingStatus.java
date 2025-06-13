package com.petcare.petcare_api.coredomain.model.scheduling.enums;

import lombok.Getter;

@Getter
public enum SchedulingStatus {
    WAITING_FOR_ARRIVAL("Aguardando chegada do pet"),
    PENDING("Pendente"),
    IN_PROGRESS("Em progresso"),
    WAITING_FOR_PICKUP("Aguardando retirada do pet"),
    COMPLETED("Finalizado"),
    NO_SHOW("Não compareceu");

    private final String descricao;

    SchedulingStatus(String descricao) {
        this.descricao = descricao;
    }

}


