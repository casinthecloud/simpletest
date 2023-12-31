package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegate;
import com.casinthecloud.simpletest.cas.CasLogin;
import com.casinthecloud.simpletest.cas.CasSAML2Login;
import com.casinthecloud.simpletest.execution.Execution;
import lombok.val;

public class MainSAMLDelegateCAS {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            val login = new CasSAML2Login(new CasDelegate(2, "CasClient", new CasLogin()));
            login.setCasPrefixUrl("http://oidc-server:8080/cas");
            return login;
        }).launch();
    }
}
