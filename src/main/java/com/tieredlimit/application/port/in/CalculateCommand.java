package com.tieredlimit.application.port.in;

import com.tieredlimit.domain.model.Settlement;
import com.tieredlimit.domain.model.TierPolicy;

public record CalculateCommand(Settlement settlement, TierPolicy tierPolicy) {
}


