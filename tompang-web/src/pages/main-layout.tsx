import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import useScrollTrigger from '@mui/material/useScrollTrigger';
import Container from '@mui/material/Container';
import Slide from '@mui/material/Slide';
import { NavLink, Outlet, useNavigate } from 'react-router';
import { useAuth } from '../context/auth-context';
import ToolbarProfile from '../components/toolbar-profile';
import ButtonGroup from '@mui/material/ButtonGroup';
import Button from '@mui/material/Button';

interface Props {
  /**
   * Injected by the documentation to work in an iframe.
   * You won't need it on your project.
   */
  window?: () => Window;
  children?: React.ReactElement<unknown>;
}

function HideOnScroll(props: Props) {
  const { children, window } = props;
  // Note that you normally won't need to set the window ref as useScrollTrigger
  // will default to window.
  // This is only being set here because the demo is in an iframe.
  const trigger = useScrollTrigger({
    target: window ? window() : undefined,
  });

  return (
    <Slide appear={false} direction="down" in={!trigger}>
      {children ?? <div />}
    </Slide>
  );
}

export default function MainLayout(props: Props) {

  const navigate = useNavigate();
  const { isAuthenticated, authToken, currentUserId, logout } = useAuth();

  return (
    <div className='flex flex-col h-full'>
      <HideOnScroll {...props}>
        <AppBar>
          <Toolbar className='flex justify-around'>
            <Typography onClick={() => navigate("/home")} variant="h6" component="div">
              Tompang Carpool
            </Typography>
            <div className='flex'>
              <NavLink
                key="carpools"
                to="/carpool"
                className={({ isActive }) => 
                  `px-2 py-1 hover:bg-zinc-100 rounded-md font-semibold
                  ${isActive ? 'text-black bg-zinc-50 border-zinc-200 border-2' : 'text-zinc-700'}`
                }
              >carpools</NavLink>
            </div>
            <div>
              {
                isAuthenticated 
                  ? <ToolbarProfile userId={currentUserId} token={authToken} onLogout={logout} /> 
                  : <ButtonGroup variant="contained" color="info">
                    <Button onClick={() => navigate("/auth/login")}>Login</Button>
                    <Button onClick={() => navigate("/auth/register")}>Register</Button>
                  </ButtonGroup>
                }
            </div>
          </Toolbar>
        </AppBar>
      </HideOnScroll>
      <Toolbar />
      <Container maxWidth='xl' className='flex-1 min-w-2xl bg-blue-100'>
        <Outlet/>
      </Container>
    </div>
  );
}