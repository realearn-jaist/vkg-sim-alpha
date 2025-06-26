import React, { useState } from 'react';
import { Box, Typography, Button, FormControl, InputLabel, Select, MenuItem, FormControlLabel, Radio, RadioGroup, Slider, TextField } from '@mui/material';
import MyCodeEditor from '../CodeEditor';
import { sendQuery } from '../../api';

const QuerySection = ({ fileNames, queryType, handleQueryTypeChange, similarityThreshold, setSimilarityThreshold }: {
  fileNames: string[],
  queryType: "standard" | "similarity",
  handleQueryTypeChange: (event: React.ChangeEvent<HTMLInputElement>) => void,
  similarityThreshold: number,
  setSimilarityThreshold: (value: number) => void
}) => {
  const [sparqlQuery, setSparqlQuery] = useState<string>('');
  const [sparqlResult, setSparqlResult] = useState<string>('');
  const [owlFilename, setOwlFilename] = useState<string>('');
  const [error, setError] = useState({ owlFilename: false, sparqlQuery: false });

  const handleQuerySend = () => {
    let hasError = false;
    if (owlFilename === '') {
      setError(prev => ({ ...prev, owlFilename: true }));
      hasError = true;
    } else {
      setError(prev => ({ ...prev, owlFilename: false }));
    }
    if (sparqlQuery === '') {
      setError(prev => ({ ...prev, sparqlQuery: true }));
      hasError = true;
    } else {
      setError(prev => ({ ...prev, sparqlQuery: false }));
    }

    if (!hasError) {
      sendQuery({ query: sparqlQuery, owlFileType: owlFilename, queryType })
        .then(response => setSparqlResult(response.data))
        .catch(error => {
          console.error('Error sending query:', error);
          setSparqlResult('Error sending query: ' + error.message);
        });
    }
  };

  return (
    <Box
      sx={{
        backgroundColor: 'white',
        borderRadius: 2,
        boxShadow: 1,
        padding: 3,
        marginBottom: 4,
      }}
    >
      <Typography variant="h6" gutterBottom>
        Query Section
      </Typography>
      <FormControl fullWidth margin="normal" error={error.owlFilename}>
        <InputLabel id="owlFilenameLabel">Select OWL Filename</InputLabel>
        <Select
          labelId="owlFilenameLabel"
          value={owlFilename}
          onChange={(e) => setOwlFilename(e.target.value as string)}
        >
          <MenuItem value=""><em>Select filename</em></MenuItem>
          {fileNames
            .filter(fileName => !fileName.endsWith('.krss'))
            .map((fileName, index) => (
              <MenuItem key={index} value={fileName}>{fileName}</MenuItem>
            ))}
        </Select>
      </FormControl>
      <FormControl component="fieldset" margin="normal">
        <RadioGroup value={queryType} onChange={handleQueryTypeChange}>
          <FormControlLabel value="standard" control={<Radio />} label="Standard Query" />
          <FormControlLabel value="similarity" control={<Radio />} label="Similarity Query (Advanced Feature)" />
        </RadioGroup>
      </FormControl>
      {queryType === 'similarity' && (
        <Box mt={2} mb={2}>
          <Typography>Similarity Threshold: {similarityThreshold}</Typography>
          <Slider
            value={similarityThreshold}
            onChange={(_e, newValue) => setSimilarityThreshold(newValue as number)}
            step={0.01}
            min={0}
            max={1}
            valueLabelDisplay="auto"
          />
        </Box>
      )}
      <Typography variant="subtitle1" gutterBottom>
        Enter SPARQL Query
      </Typography>
      <MyCodeEditor
        value={sparqlQuery}
        handler={(value: string) => setSparqlQuery(value)}
      />
      <Button
        variant="contained"
        sx={{ bgcolor: '#00acc1', color: 'white', '&:hover': { bgcolor: '#00838f' }, mt: 2, mb: 2, borderRadius: "8px" }}
        onClick={handleQuerySend}
      >
        Send Query
      </Button>
      <TextField
        label="Query Result"
        multiline
        rows={10}
        value={sparqlResult}
        variant="outlined"
        fullWidth
        margin="normal"
        InputProps={{
          readOnly: true,
        }}
        sx={{
          bgcolor: '#f9f9f9',
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: '#cccccc',
            },
            '&:hover fieldset': {
              borderColor: '#888888',
            },
            '&.Mui-focused fieldset': {
              borderColor: '#00bcd4',
            },
          },
        }}
      />
    </Box>
  );
};

export default QuerySection;
