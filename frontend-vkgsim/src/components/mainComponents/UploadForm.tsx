import React, { useState } from 'react';
import { Button, TextField, Box, Typography, Switch, FormControlLabel, IconButton, Snackbar, SnackbarContent } from '@mui/material';
import { Close, Info } from '@mui/icons-material';

const UploadForm = ({ isLoadFolder, handleDeleteProfile, handleUploadFile }: {
  isLoadFolder: boolean, handleDeleteProfile: () => void,
  handleUploadFile: (owlFile: File, mappingFile: File, propertiesFile: File, driverFile: File, apiKey: string) => void
}) => {
  const [owlFile, setOwlFile] = useState<File | null>(null);
  const [mappingFile, setMappingFile] = useState<File | null>(null);
  const [propertiesFile, setPropertiesFile] = useState<File | null>(null);
  const [driverFile, setDriverFile] = useState<File | null>(null);
  const [useNaturalLanguage, setUseNaturalLanguage] = useState<boolean>(false);
  const [apiKey, setApiKey] = useState<string>('');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);

  const handleFileChange = (setter: React.Dispatch<React.SetStateAction<File | null>>) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setter(event.target.files ? event.target.files[0] : null);
  };

  const handleSubmit = () => {
    if (owlFile && mappingFile && propertiesFile && driverFile) {
      if (useNaturalLanguage && apiKey.length > 0) {
        handleUploadFile(owlFile, mappingFile, propertiesFile, driverFile, apiKey);
      } else {
        handleUploadFile(owlFile, mappingFile, propertiesFile, driverFile, "");
      }
    }
  };

  const handleInfoClick = () => {
    setSnackbarOpen(true);
  };

  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };

  return (
    <Box
      sx={{
        backgroundColor: 'white',
        borderRadius: 2,
        boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
        padding: '20px',
        marginBottom: '20px'
      }}
    >
      <Typography variant="h6">Upload Section</Typography>
      <Box mt={2}>
        <TextField
          type="file"
          onChange={handleFileChange(setOwlFile)}
          label="OWL File"
          fullWidth
          InputLabelProps={{ shrink: true }}
          inputProps={{ accept: ".owl,.krss" }}
          disabled={!isLoadFolder}
        />
      </Box>
      <Box mt={2}>
        <TextField
          type="file"
          onChange={handleFileChange(setMappingFile)}
          label="Mapping File"
          fullWidth
          InputLabelProps={{ shrink: true }}
          inputProps={{ accept: ".obda" }}
          disabled={!isLoadFolder}
        />
      </Box>
      <Box mt={2} display="flex" alignItems="center">
        <TextField
          type="file"
          onChange={handleFileChange(setPropertiesFile)}
          label="Properties File"
          fullWidth
          InputLabelProps={{ shrink: true }}
          inputProps={{ accept: ".properties" }}
          disabled={!isLoadFolder}
        />
        <IconButton onClick={handleInfoClick}>
          <Info />
        </IconButton>
      </Box>
      <Box mt={2}>
        <TextField
          type="file"
          onChange={handleFileChange(setDriverFile)}
          label="Driver File"
          fullWidth
          InputLabelProps={{ shrink: true }}
          inputProps={{ accept: ".jar" }}
          disabled={!isLoadFolder}
        />
      </Box>

      <Snackbar
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
      >
        <SnackbarContent
          style={{ backgroundColor: '#323232', color: '#fff', padding: '16px', borderRadius: 8 }}
          message={
            <Box>
              <Box style={{ position: 'absolute', right: '8px', top: '8px' }}>
                <IconButton size="small" color="inherit" onClick={handleSnackbarClose}>
                  <Close fontSize="small" />
                </IconButton>
              </Box>
              <Typography variant="h6" gutterBottom><strong>Instructions:</strong></Typography>
              <Typography variant="body1">1. Create a file .properties.</Typography>
              <Typography variant="body1">2. Use the following syntax:</Typography>
              <Box ml={2}>
                <Typography variant="body2">- jdbc.url=jdbc:h2:tcp://localhost/../university-session1</Typography>
                <Typography variant="body2">- jdbc.driver=org.h2.Driver</Typography>
                <Typography variant="body2">- jdbc.user=</Typography>
                <Typography variant="body2">- jdbc.password=</Typography>
              </Box>
              <Typography variant="body1">3. Upload the file</Typography>
            </Box>
          }
        />
      </Snackbar>

      <Box mt={2}>
        <FormControlLabel
          control={<Switch checked={useNaturalLanguage} onChange={(e) => setUseNaturalLanguage(e.target.checked)} disabled={!isLoadFolder} />}
          label="Display explanation in natural language"
        />
      </Box>
      {useNaturalLanguage && (
        <Box mt={2}>
          <TextField
            type="password"
            label="OpenAI API Key"
            fullWidth
            value={apiKey}
            onChange={(e) => setApiKey(e.target.value)}
          />
        </Box>
      )}
      <Box mt={2} display="flex" justifyContent="space-between">
        <Button
          variant="contained"
          sx={{ bgcolor: '#0097a7', color: 'white', '&:hover': { bgcolor: '#00838f' } }}
          onClick={handleSubmit}
          disabled={!isLoadFolder}
        >
          Upload Files
        </Button>
        <Button
          variant="contained"
          sx={{ bgcolor: '#d32f2f', color: 'white', '&:hover': { bgcolor: '#c62828' } }}
          onClick={handleDeleteProfile}
          disabled={!isLoadFolder}
        >
          Delete Profile
        </Button>
      </Box>
    </Box>
  );
};

export default UploadForm;
