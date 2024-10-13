package tingeso.prestabanco.dto;


import lombok.Data;

@Data
public class SavingCapacity {
    private Long client_balance;
    private boolean has_consistent_savings;
    private boolean has_periodic_savings;
    private int savings_account_longevity;
    private boolean is_financially_stable;
}
