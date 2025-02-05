package guru.qa.niffler.service.impl;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CONFIG = Config.getInstance();
    private final UserdataSoapApi soapApi;

    public UserdataSoapClient() {
        super(CONFIG.userdataUrl(), SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        soapApi = create(UserdataSoapApi.class);
    }

    @NotNull
    @Step("Fetch current user details via SOAP")
    public UserResponse getUserInfo(CurrentUserRequest request) throws IOException {
        return requireNonNull(soapApi.getUser(request).execute().body());
    }

    @Step("Retrieve list of friends via SOAP")
    public UsersResponse getFriendsList(FriendsPageRequest request) {
        try {
            return soapApi.fetchFriends(request).execute().body();
        } catch (IOException e) {
            throw new RuntimeException("Error retrieving friends list", e);
        }
    }

    @Step("Send a friend invitation")
    public UserResponse sendRequest(SendInvitationRequest request) {
        try {
            return soapApi.initiateFriendRequest(request).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Accept a friend request")
    public UserResponse confirmFriendRequest(AcceptInvitationRequest request) {
        try {
            return soapApi.approveRequest(request).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Decline a friend invitation")
    public UserResponse rejectRequest(DeclineInvitationRequest request) {
        try {
            return soapApi.refuseInvitation(request).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Remove a friend from list")
    public void deleteFriend(RemoveFriendRequest request) {
        try {
            soapApi.deleteFriendship(request).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}