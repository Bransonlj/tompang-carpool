import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { jwtDecode, type JwtPayload } from "jwt-decode";
import { useSocket } from "./socket-context";
import type { LoginRequestDto, RegisterRequestDto, UserRole } from "../api/services/auth/types";
import { authLogin, authRegister } from "../api/services/auth/auth.service";

interface CustomJwtPayload extends JwtPayload {
  roles: UserRole[];
  exp: number;
  sub: string;
}

interface TBaseAuthContext {
  login: (input: LoginRequestDto) => Promise<boolean>;
  register: (input: RegisterRequestDto) => Promise<boolean>;
  logout: () => void;
  loginError: string;
  registerError: string;
  isLoginPending: boolean;
  isRegisterPending: boolean;
  isUser: boolean;
  isAdmin: boolean;
  isDriver: boolean;
}

interface UnauthenticatedAuthState {
  currentUserId: null;
  currentUserRoles: null;
  authToken: null;
  isAuthenticated: false;
}

interface AuthenticatedAuthState {
  currentUserId: string;
  currentUserRoles: UserRole[];
  authToken: string;
  isAuthenticated: true;
}

type TAuthContext = (UnauthenticatedAuthState | AuthenticatedAuthState) & TBaseAuthContext;

const LocalStorageUserKey = 'auth-user-token'

const AuthContext = createContext<TAuthContext>({
  currentUserId: null,
  currentUserRoles: null,
  authToken: null,
  isAuthenticated: false,
  login: async () => false, // Default value to indicate that login failed
  register: async () => false, // Default value to indicate that registration failed
  logout: () => {}, // Default empty function for logout
  loginError: '',
  registerError: '',
  isLoginPending: false,
  isRegisterPending: false,
  isAdmin: false,
  isUser: false,
  isDriver: false,
});

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }: { children: React.ReactNode}) {

  const [authState, setAuthState] = useState<UnauthenticatedAuthState | AuthenticatedAuthState>({
    currentUserId: null,
    authToken: null,
    currentUserRoles: null,
    isAuthenticated: false,
  })

  const [loginError, setLoginError] = useState<string>("");
  const [isLoginPending, setLoginPending] = useState<boolean>(false);
  const [registerError, setRegisterError] = useState<string>(""); 
  const [isRegisterPending, setRegisterPending] = useState<boolean>(false);

  // const { connect, disconnect } = useSocket(); // TODO use socket for notif

  const login = useCallback(async (loginDto: LoginRequestDto): Promise<boolean> => {
    setLoginError("");
    setLoginPending(true);
    try {
      const data = await authLogin(loginDto);
      // successful login
      setAuthState({
        currentUserId: data.userId,
        authToken: data.token,
        currentUserRoles: data.roles,
        isAuthenticated: true,
      });
      // save token to cookies
      localStorage.setItem(LocalStorageUserKey, data.token);
      setLoginPending(false);
      //connect(data.userId);
      return true;
    } catch (error: any) {
      setLoginError(error.message);
      setLoginPending(false);
      return false;
    }
  }, []);

  const register = useCallback(async (registerDto: RegisterRequestDto): Promise<boolean> => {
    setRegisterError("");
    setRegisterPending(true);
    try {
      await authRegister(registerDto);
      // success
      setRegisterPending(false);
      return true;
    } catch (error: any) {
      setRegisterError(error.message);
      setRegisterPending(false);
      return false
    }
  }, []);

  /**
   * Remove token from localstorage and reset auth react state to nulls.
   */
  const logout = useCallback((): void => {
    setAuthState({
      currentUserId: null,
      currentUserRoles: null,
      authToken: null,
      isAuthenticated: false,
    });
    localStorage.removeItem(LocalStorageUserKey);
    //disconnect();
  }, []);

  const isUser = useMemo(() => authState.currentUserRoles?.includes("USER") ?? false, [authState]);
  const isDriver = useMemo(() => authState.currentUserRoles?.includes("DRIVER") ?? false, [authState]);
  const isAdmin = useMemo(() => authState.currentUserRoles?.includes("ADMIN") ?? false, [authState]);

  useEffect(() => {
    // Check local storage for token & try to login with it
    const token = (localStorage.getItem(LocalStorageUserKey));
    if (token) {
      try {
        const payload  = jwtDecode(token) as CustomJwtPayload;
        if (payload.exp * 1000 < Date.now()) {
          // jtw expired
          localStorage.removeItem(LocalStorageUserKey);
          alert("login session has expired");
          logout()
        } else {
          // login from cookies
          setAuthState({
            currentUserId: payload.sub,
            currentUserRoles: payload.roles,
            authToken: token,
            isAuthenticated: true,
          });
          //connect(user.userId);
        }
      } catch (error) {
        // cant decode
        localStorage.removeItem(LocalStorageUserKey);
        console.warn("Error decoding jwt token from storage");
        logout();
      }
    }
  }, []);

  const value = useMemo(
    () => ({
      ...authState,
      login,
      register,
      logout,
      loginError,
      registerError,
      isLoginPending,
      isRegisterPending,
      isUser,
      isAdmin,
      isDriver,
    }), [
      authState,
      login,
      register,
      logout,
      loginError,
      registerError,
      isLoginPending,
      isRegisterPending,
      isUser,
      isAdmin,
      isDriver,
    ]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}