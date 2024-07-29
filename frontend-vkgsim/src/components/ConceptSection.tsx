import { List, ListItem, ListItemText, Typography, Box, Paper } from '@mui/material';

const ConceptSection = ({ conceptNames }: { conceptNames: string[] }) => {
  return (
    <Box mb={4}>
      
      <Paper sx={{ height: "50vh", overflow: "auto", p: 2, bgcolor: '#ffffff', borderRadius: 2, boxShadow: 3 }}>
      <Typography variant="h6" mb={2} sx={{ textAlign: 'center' }}>All Concept Names</Typography>
        <List>
          {conceptNames.map((concept, index) => (
            <Paper
              key={index}
              sx={{
                mb: 1,
                p: 1,
                cursor: 'pointer',
                backgroundColor: '#f9f9f9',
                '&:hover': {
                  bgcolor: '#e0f7fa',
                },
              }}
            >
              <ListItem>
                <ListItemText primary={concept} />
              </ListItem>
            </Paper>
          ))}
        </List>
      </Paper>
    </Box>
  );
};

export default ConceptSection;
