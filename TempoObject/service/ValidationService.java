package TempoObject.service;

import TempoObject.model.ValidationError;
import TempoObject.Tempo;
import TempoObject.Pekerja;
import TempoObject.Transaksi;
import java.util.*;

public class ValidationService {
    public List<ValidationError> validateTempo(String fileName, Tempo tempo) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Validate total tempo matches sum of worker totals
        int calculatedTotal = tempo.getSetPekerja().stream()
                .mapToInt(Pekerja::getTotalUtang)
                .sum();
                
        if (tempo.hasTotalTempo() && calculatedTotal != tempo.getTotalTempo()) {
            errors.add(new ValidationError(
                fileName,
                -1, // Line number would need to be passed from the parser
                String.format("Total mismatch: calculated=%d, recorded=%d", 
                    calculatedTotal, tempo.getTotalTempo()),
                ValidationError.ErrorType.CALCULATION_ERROR
            ));
        }

        // Validate each worker's transactions
        for (Pekerja pekerja : tempo.getSetPekerja()) {
            errors.addAll(validatePekerja(fileName, pekerja));
        }

        return errors;
    }

    private List<ValidationError> validatePekerja(String fileName, Pekerja pekerja) {
        List<ValidationError> errors = new ArrayList<>();
        
        int calculatedTotal = pekerja.getListTransaksi().stream()
                .mapToInt(Transaksi::getHutang)
                .sum();
                
        if (calculatedTotal != pekerja.getTotalUtang()) {
            errors.add(new ValidationError(
                fileName,
                -1,
                String.format("Worker %s total mismatch: calculated=%d, recorded=%d",
                    pekerja.getNama(), calculatedTotal, pekerja.getTotalUtang()),
                ValidationError.ErrorType.TRANSACTION_TOTAL_MISMATCH
            ));
        }

        return errors;
    }
}
