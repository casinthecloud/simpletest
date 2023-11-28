package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.test.CasTest;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * A test performing a CAS login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasLoginTest extends CasTest {

    private String serviceUrl = "http://localhost:8081/";

    public void run(final Map<String, Object> ctx) throws Exception {

        val loginUrl = addUrlParameter(getCasPrefixUrl() + "/login", "service", getServiceUrl());
        login(ctx, loginUrl);

        validateSt(ctx);

    }

    public void login(final Map<String, Object> ctx, final String loginUrl) throws Exception {

        callLoginPage(ctx, loginUrl);

        if (_status == 200) {
            postCredentials(ctx, loginUrl);
        }

        assertStatus(302);
    }

    public void callLoginPage(final Map<String, Object> ctx, final String loginUrl) throws Exception {
        val tgc = (Pair<String, String>) ctx.get(TGC);

        if (tgc != null) {
            info("Re-use: " + tgc.getLeft() + "=" + tgc.getRight());
            _cookies.put(getCasCookieName(), tgc.getRight());
        }

        _request = get(loginUrl);
        execute();
    }

    public void postCredentials(final Map<String, Object> ctx, final String loginUrl) throws Exception {
        val webflow = substringBetween(_body, "name=\"execution\" value=\"", "\"/>");

        _data.put("username", getUsername());
        _data.put("password", getPassword());
        _data.put("execution", webflow);
        _data.put("_eventId", "submit");
        _data.put("geolocation", "");

        _request = post(loginUrl);
        execute();

        val tgc = getCookie(getCasCookieName());
        ctx.put(TGC, tgc);
        info("Found: " + tgc.getLeft() + "=" + tgc.getRight());
    }

    public void validateSt(final Map<String, Object> ctx) throws Exception {
        val callbackUrl = getLocation();
        val st = substringAfter(callbackUrl, "ticket=");

        var validateUrl = getCasPrefixUrl() + "/p3/serviceValidate";
        validateUrl = addUrlParameter(validateUrl, "service", getServiceUrl());
        validateUrl = addUrlParameter(validateUrl, "ticket", st);

        _request = get(validateUrl);
        execute();
        assertStatus(200);
    }
}
