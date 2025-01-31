package guru.qa.niffler.test.soap;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UserdataSoapClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SoapTest
class SoapUsersTest {

    private final UserdataSoapClient soapClient = new UserdataSoapClient();

    @Test
    @User
    void shouldReturnUserData(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());

        UserResponse response = soapClient.getUserInfo(request);

        assertNotNull(response);
        assertEquals(user.username(), response.getUser().getUsername());
    }

    @Test
    @User(friends = 1)
    void shouldSuccessfullyRemoveFriend(UserJson user) throws IOException {
        RemoveFriendRequest request = new RemoveFriendRequest();
        request.setUsername(user.username());
        request.setFriendToBeRemoved(user.testData().friends().getFirst().username());

        soapClient.deleteFriend(request);

        CurrentUserRequest verifyRequest = new CurrentUserRequest();
        verifyRequest.setUsername(user.username());
        UserResponse response = soapClient.getUserInfo(verifyRequest);

        assertEquals(FriendshipStatus.VOID, response.getUser().getFriendshipStatus());
    }

    @Test
    @User(incomeInvitations = 1)
    void shouldSendFriendInvitation(UserJson user) {
        SendInvitationRequest request = new SendInvitationRequest();
        request.setUsername(user.username());
        request.setFriendToBeRequested(user.testData().incomeInvitations().getFirst().username());

        UserResponse response = soapClient.sendRequest(request);

        assertEquals(FriendshipStatus.INVITE_SENT, response.getUser().getFriendshipStatus());
    }

    @Test
    @User(incomeInvitations = 1)
    void shouldApproveFriendRequest(UserJson user) {
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setUsername(user.username());
        request.setFriendToBeAdded(user.testData().incomeInvitations().getFirst().username());

        UserResponse response = soapClient.confirmFriendRequest(request);

        assertEquals(FriendshipStatus.FRIEND, response.getUser().getFriendshipStatus());
    }

    @Test
    @User(outcomeInvitations = 1)
    void shouldRejectIncomingInvitation(UserJson user) {
        DeclineInvitationRequest request = new DeclineInvitationRequest();
        request.setUsername(user.username());
        request.setInvitationToBeDeclined(user.testData().outcomeInvitations().getFirst().username());

        UserResponse response = soapClient.rejectRequest(request);

        assertEquals(FriendshipStatus.VOID, response.getUser().getFriendshipStatus());
    }

    @ParameterizedTest
    @User(
            friends = 10
    )
    @CsvSource({
            "0, 8, 8",
            "1, 8, 2",
            "2, 8, 0"
    })
    void shouldRetrieveFriendsWithPagination(int page, int size, int expectedCount, UserJson user) {
        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(user.username());

        PageInfo pagination = new PageInfo();
        pagination.setPage(page);
        pagination.setSize(size);
        request.setPageInfo(pagination);

        UsersResponse response = soapClient.getFriendsList(request);

        assertEquals(expectedCount, response.getUser().size());
    }
}