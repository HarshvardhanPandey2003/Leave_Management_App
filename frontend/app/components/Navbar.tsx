// app/components/Navbar.tsx
"use client";

import Link from "next/link";
import { useAuth } from "../context/AuthContext";
import { AppBar, Toolbar, Typography, Button, Box } from "@mui/material";
import { useRouter } from "next/navigation";

const Navbar = () => {
  const { user, logout } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push("/auth/login");
  };

  return (
    <AppBar position="static" sx={{ backgroundColor: "#1976d2" }}>
      <Toolbar>
        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 1, color: "inherit", textDecoration: "none" }}
        >
          <Link href="/" style={{ color: "inherit", textDecoration: "none" }}>
            Leave Management
          </Link>
        </Typography>
        <Box>
          {user ? (
            <>
              {user.role === "ADMIN" ? (
                <>
                  <Button
                    color="inherit"
                    component={Link}
                    href="/admin/approve"
                  >
                    Approve Leave
                  </Button>
                  <Button
                    color="inherit"
                    component={Link}
                    href="/admin/update-balance"
                  >
                    Update Leave Balance
                  </Button>
                </>
              ) : (
                <>
                  <Button
                    color="inherit"
                    component={Link}
                    href="/leave/apply"
                  >
                    Apply Leave
                  </Button>
                  <Button
                    color="inherit"
                    component={Link}
                    href="/leave/history"
                  >
                    Leave History
                  </Button>
                  <Button
                    color="inherit"
                    component={Link}
                    href="/leave/balance"
                  >
                    Leave Balance
                  </Button>
                </>
              )}
              <Button color="inherit" onClick={handleLogout}>
                Logout
              </Button>
            </>
          ) : (
            <>
              <Button color="inherit" component={Link} href="/auth/login">
                Login
              </Button>
              <Button color="inherit" component={Link} href="/auth/signup">
                Signup
              </Button>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
