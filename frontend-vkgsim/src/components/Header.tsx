import { AppBar, Toolbar, IconButton, Box, Button, TextField, Typography } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { useState } from 'react';

const Header = ({ toggleSidebar, open, setUser }: { toggleSidebar: () => void, open: boolean, setUser: (username: string) => void }) => {
    const [username, setUsername] = useState<string>('');

    const handleCreateFolder = () => {
        if (username.trim() === '') {
            alert('Username cannot be empty.');
        } else if (/\s/.test(username)) {
            alert("Username should not contain spaces.");
        } else {
            setUser(username);
        }
    };

    return (
        <AppBar position="static" sx={{ backgroundColor: '#333' }}>
            <Toolbar sx={{ justifyContent: 'space-between' }}>
                <Box display="flex" alignItems="center">
                    {!open && (
                        <IconButton
                            color="inherit"
                            aria-label="open drawer"
                            edge="start"
                            onClick={toggleSidebar}
                        >
                            <MenuIcon />
                        </IconButton>
                    )}
                    {!open && (
                        <Typography variant="h6" sx={{ marginLeft: 2 }}>
                            VKGSim
                        </Typography>
                    )}
                </Box>

                <Box display="flex" alignItems="center">
                    <TextField
                        label="Username"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        sx={{ marginRight: 2, bgcolor: 'white', borderRadius: 1 }}
                        size="small"
                        variant="outlined"
                        InputProps={{
                            style: { backgroundColor: 'white' }
                        }}
                    />
                    <Button 
                        variant="contained" 
                        sx={{ bgcolor: '#00acc1', color: 'white', '&:hover': { bgcolor: '#00838f' } }}
                        onClick={handleCreateFolder}
                    >
                        Submit
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Header;
