package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.decrLevel;
import static com.casinthecloud.simpletest.util.Utils.incrLevel;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * A test performing a CAS authn delegation in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasDelegate extends CasLogin {

    private final int nbCallbacks;

    private final String clientName;

    public CasDelegate(final int nbCallbacks, final String clientName, final CasTest casTest) {
        this.nbCallbacks = nbCallbacks;
        this.tests = new CasTest[] { casTest };
        this.clientName = clientName;
    }

    public void run(final Context ctx) throws Exception {

        info("> BEGIN CasDelegate");

        val loginUrl = callLoginPage(ctx);

        delegate(ctx, loginUrl);

        incrLevel();
        this.tests[0].run(ctx);
        decrLevel();

        for (int i = 0; i < nbCallbacks; i++) {
            callback(ctx, 302);
        }

        info("< END CasDelegate");

    }

    protected void delegate(final Context ctx, final String loginUrl) throws Exception {
        val webflow = substringBetween(ctx.getBody(), "name=\"execution\" value=\"", "\"/>");

        val fp = ctx.getFormParameters();
        fp.put("client_name", getClientName());
        fp.put("_eventId", "delegatedAuthenticationRedirect");
        fp.put("execution", webflow);

        ctx.setRequest(post(ctx, loginUrl));
        execute(ctx);

        saveCasSession(ctx);
    }
}
