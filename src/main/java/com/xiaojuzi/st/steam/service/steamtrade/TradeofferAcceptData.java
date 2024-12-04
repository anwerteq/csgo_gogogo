package com.xiaojuzi.st.steam.service.steamtrade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TradeofferAcceptData {

    private String tradeid;
    @JsonProperty("needs_mobile_confirmation")
    private boolean needsMobileConfirmation;
    @JsonProperty("needs_email_confirmation")
    private boolean needsEmailConfirmation;
    @JsonProperty("email_domain")
    private String emailDomain;

}
