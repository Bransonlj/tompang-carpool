import { Routes, Route, Navigate, Outlet } from "react-router";
import MainLayout from "./pages/main-layout";
import CarpoolListPage from "./modules/carpool/pages/carpool-list";
import LoginPage from "./pages/login-page";
import RegisterPage from "./pages/register-page";
import { useAuth } from "./context/auth-context";
import HomePage from "./pages/home-page";
import CarpoolDetailPage from "./modules/carpool/pages/carpool-detail";
import RideRequestDetailPage from "./modules/carpool/pages/ride-request-detail";
import UserProfilePage from "./modules/user/pages/user-profile";
import AccountSettingsPage from "./modules/user/pages/account-settings";

function RequireAuth() {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/auth/login" replace />;
  }

  return <Outlet />;
}

function UnauthenticatedOnly() {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}

export default function Router() {
  return (
    <Routes>
      <Route element={<MainLayout />} >
        <Route index element={<Navigate to="/home" replace />} />
        <Route path="home" element={<HomePage />} />
          <Route path="auth" element={<UnauthenticatedOnly />}>
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
          </Route>
          
          {/** Protected routes */}
          <Route element={<RequireAuth />}>
            <Route path="u">
              <Route path="account" element={<AccountSettingsPage />} />
              <Route path=":id" element={<UserProfilePage />} />
            </Route>
            <Route path="carpool">
              <Route index element={<CarpoolListPage />} />
              <Route path="ride/:id" element={<RideRequestDetailPage />} />
              <Route path=":id" element={<CarpoolDetailPage />} />
            </Route>
          </Route>
      </Route>
    </Routes>
  )
}