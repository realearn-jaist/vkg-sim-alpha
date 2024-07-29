import { Box, Divider, IconButton, List, ListItem, ListItemIcon, ListItemText, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import Menu from '@mui/icons-material/Menu';
import { Map, Search, FileUpload } from '@mui/icons-material';

const Sidebar = ({ open, setOpen }: { open: boolean, setOpen: (value: boolean) => void }) => {
  return (
    <Box sx={{ width: "100%", bgcolor: 'grey.900', color: 'white', height:"100%" }} role="presentation">
      <Box display={"flex"} height={"65px"} alignItems={"center"} px={2}>
        <Typography variant="h5" sx={{ flexGrow: 1, color: 'white' }}>VKGSim</Typography>
        <IconButton onClick={() => setOpen(!open)} sx={{ color: 'white' }}>
          <Menu />
        </IconButton>
      </Box>
      <Divider sx={{ borderColor: 'grey.700' }} />
      <List>
        <ListItem component={Link} to="/mainPage" button>
          <ListItemIcon sx={{ color: 'white' }}>
            <FileUpload />
          </ListItemIcon>
          <ListItemText primary="Upload Section" />
        </ListItem>
        <ListItem component={Link} to="/mappingPage" button>
          <ListItemIcon sx={{ color: 'white' }}>
            <Map />
          </ListItemIcon>
          <ListItemText primary="Mapping Section" />
        </ListItem>
        <ListItem component={Link} to="/queryPage" button>
          <ListItemIcon sx={{ color: 'white' }}>
            <Search />
          </ListItemIcon>
          <ListItemText primary="Query Section" />
        </ListItem>
      </List>
    </Box>
  );
};

export default Sidebar;
