import { Button, TextField, Box, Typography } from '@mui/material';
import { saveMapping } from '../../api';
import MyCodeEditor from '../CodeEditor';
import { useState } from 'react';


const MappingSection = ({ mapping, setMapping, baseIRI, setBaseIRI, handleGenerateMapping }: {
  mapping: string, setMapping: (value: string) => void, baseIRI: string, setBaseIRI: (value: string) => void, handleGenerateMapping: () => void
}) => {

  const [message, setMessage] = useState<string>('');
  const [messageType, setMessageType] = useState<string>(''); // 'success' or 'error'
  const [isError, setIsError] = useState(false);

  const handleSaveMapping = () => {
    saveMapping(mapping)
      .then(() => {
        setMessage('Mapping saved successfully.');
        setMessageType('success');
      })
      .catch(error => {
        setMessage('Error saving mapping: ' + error.message);
        setMessageType('error');
      });
  };

  const handleChange = (e: { target: { value: any; }; }) => {
    const value = e.target.value;
    setBaseIRI(value);

    // Regex to validate the base IRI format: http://<anything>/
    // where <anything> can include letters, digits, dots, dashes, and underscores
    const regex = /^http:\/\/[a-zA-Z0-9._-]+\/$/;
    setIsError(!regex.test(value));
  };

  return (
    <Box sx={{ padding: 3, bgcolor: '#ffffff', borderRadius: 2, boxShadow: 1, mb: 4 }}>
      <Typography variant="h6" sx={{ color: '#333', marginBottom: 2 }}>
        Mapping Section
      </Typography>
      <Box display="flex" alignItems="center" mb={2}>
        <TextField
          label="Base IRI"
          value={baseIRI}
          onChange={handleChange}
          fullWidth
          error={isError}
          helperText={isError ? "Invalid format. Use http://<anything>/ don't include '>' and '<'" : ''}
          sx={{
            bgcolor: '#f9f9f9',
            borderRadius: 2,
            marginRight: 2,
            '& .MuiOutlinedInput-root': {
              '& fieldset': {
                borderColor: '#cccccc',
              },
              '&:hover fieldset': {
                borderColor: '#888888',
              },
              '&.Mui-focused fieldset': {
                borderColor: '#00bcd4', // Cyan shade for focus
              },
            },
          }}
          placeholder="Enter base IRI in format: http://example/ and don't include '>' and '<'"
        />
        <Button
          onClick={handleGenerateMapping}
          variant="contained"
          sx={{ backgroundColor: '#00acc1', color: 'white', '&:hover': { backgroundColor: '#00838f' }, borderRadius: "8px" }} // Cyan color scheme
        >
          Generate Mapping
        </Button>
      </Box>
      <Box sx={{ marginBottom: 2 }}>
        <Typography variant="body1" sx={{ marginBottom: 1 }}>
          Mapping Editor
        </Typography>
        <Box sx={{ border: '1px solid #cccccc', borderRadius: 1, overflow: 'hidden' }}>
          <MyCodeEditor value={mapping} handler={setMapping} />
        </Box>
      </Box>
      <Button
        onClick={handleSaveMapping}
        variant="contained"
        sx={{ backgroundColor: '#00acc1', color: 'white', '&:hover': { backgroundColor: '#00838f' }, borderRadius: "8px" }} // Cyan color scheme
      >
        Save Mapping
      </Button>
      {message && (
        <Typography
          variant="body2"
          sx={{
            color: messageType === 'success' ? 'green' : 'red',
            marginTop: 2
          }}
        >
          {message}
        </Typography>
      )}
    </Box>
  );
};

export default MappingSection;
