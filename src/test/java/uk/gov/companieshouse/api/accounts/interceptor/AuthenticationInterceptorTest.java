package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.api.util.security.EricConstants;
import uk.gov.companieshouse.api.util.security.Permission.Key;
import uk.gov.companieshouse.api.util.security.Permission.Value;
import uk.gov.companieshouse.api.util.security.SecurityConstants;
import uk.gov.companieshouse.api.util.security.TokenPermissions;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTest {

    @Spy
    private AuthenticationInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ModelAndView modelAndView;

    @Mock
    private TokenPermissions tokenPermissions;

    private final Object handler = null;

    @Test
    @DisplayName("Test preHandle when request is authorized")
    void preHandleAuthorized() throws Exception {
        setupTokenPermissions();
        final boolean hasCompanyAccountsUpdatePermission = true;
        when(tokenPermissions.hasPermission(Key.COMPANY_ACCOUNTS, Value.UPDATE))
                .thenReturn(hasCompanyAccountsUpdatePermission);

        assertTrue(interceptor.preHandle(request, response, handler));
    }

    @Test
    @DisplayName("Test preHandle when request is unauthorized")
    void preHandleUnauthorized() throws Exception {
        setupTokenPermissions();
        final boolean hasCompanyAccountsUpdatePermission = false;
        when(tokenPermissions.hasPermission(Key.COMPANY_ACCOUNTS, Value.UPDATE))
                .thenReturn(hasCompanyAccountsUpdatePermission);

        assertFalse(interceptor.preHandle(request, response, handler));
    }

    @Test
    @DisplayName("Test preHandle when TokenPermissions is not present in request")
    void preHandleMissingTokenPermissions() throws Exception {
        assertThrows(IllegalStateException.class, () -> interceptor.preHandle(request, response, handler));
    }

    @Test
    @DisplayName("Test preHandle when request is authorized with an api key")
    void preHandleAuthorizedAPIKey() throws Exception {
        doReturn(SecurityConstants.API_KEY_IDENTITY_TYPE).when(request).getHeader(EricConstants.ERIC_IDENTITY_TYPE);
        assertTrue(interceptor.preHandle(request, response, handler));
    }

    private void setupTokenPermissions() {
        doReturn(Optional.of(tokenPermissions)).when(interceptor).getTokenPermissions(request);
    }

    @Test
    void testPostHandleAndAfterCompletion() throws Exception {
        Object handler = new Object();
        Exception ex = new Exception();
        ModelAndView modelAndView = new ModelAndView();

        AuthenticationInterceptor interceptor = new AuthenticationInterceptor();
        AuthenticationInterceptor spyInterceptor = Mockito.spy(interceptor);
        spyInterceptor.postHandle(request, response, handler, modelAndView);
        spyInterceptor.afterCompletion(request, response, handler, ex);
        Mockito.verify(spyInterceptor, Mockito.times(1)).postHandle(request, response, handler, modelAndView);
        Mockito.verify(spyInterceptor, Mockito.times(1)).afterCompletion(request, response, handler, ex);
    }
}
