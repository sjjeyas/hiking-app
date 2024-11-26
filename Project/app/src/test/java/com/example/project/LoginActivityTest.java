//package com.example.project;
//
//import static org.mockito.Mockito.*;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//public class LoginActivityTest {
//
//    private LoginActivity loginActivity;
//
//    @Mock
//    private FirebaseAuth mockFirebaseAuth;
//
//    @Mock
//    private Task<AuthResult> mockAuthResultTask;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        loginActivity = spy(new LoginActivity());
//
//        // Mock FirebaseAuth instance
//        doReturn(mockFirebaseAuth).when(loginActivity).getFirebaseAuthInstance();
//    }
//
//    @Test
//    public void testLoginSuccess() {
//        // Arrange
//        doReturn("test@example.com").when(loginActivity).getEmailInput();
//        doReturn("password123").when(loginActivity).getPasswordInput();
//
//        // Mock successful login
//        when(mockAuthResultTask.isSuccessful()).thenReturn(true);
//
//        ArgumentCaptor<OnCompleteListener<AuthResult>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);
//
//        // Act
//        loginActivity.loginUserAccount();
//
//        // Capture the callback and simulate success
//        verify(mockFirebaseAuth).signInWithEmailAndPassword(anyString(), anyString());
//        verify(mockAuthResultTask).addOnCompleteListener(captor.capture());
//        captor.getValue().onComplete(mockAuthResultTask);
//
//        // Assert
//        verify(loginActivity).navigateToMainActivity();
//    }
//
//    @Test
//    public void testLoginFailure() {
//        // Arrange
//        doReturn("test@example.com").when(loginActivity).getEmailInput();
//        doReturn("password123").when(loginActivity).getPasswordInput();
//
//        // Mock failed login
//        when(mockAuthResultTask.isSuccessful()).thenReturn(false);
//
//        ArgumentCaptor<OnCompleteListener<AuthResult>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);
//
//        // Act
//        loginActivity.loginUserAccount();
//
//        // Capture the callback and simulate failure
//        verify(mockFirebaseAuth).signInWithEmailAndPassword(anyString(), anyString());
//        verify(mockAuthResultTask).addOnCompleteListener(captor.capture());
//        captor.getValue().onComplete(mockAuthResultTask);
//    }
//}
//
