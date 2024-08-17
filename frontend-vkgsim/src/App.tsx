// App.tsx
import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { Box, CssBaseline } from '@mui/material';
import Header from './components/Header'; // Ensure the import path is correct
import Sidebar from './components/Sidebar'; // Ensure the import path is correct
import MainPage from './views/MainPage';
import MappingPage from './views/MappingPage';
import QueryPage from './views/QueryPage';
import { createUserFolder, deleteProfile, findAllConceptNames, generateMapping, getMappingID, getOWLFilename, readBaseIRI, readMappingFileContent, readSimilarityFileContent, similarityMeasureAllConcept, uploadFiles } from './api';

interface DescriptionTree {
  conceptName: string;
  primitiveConcepts: string[];
  roleName?: string;
  existentials?: DescriptionTree[];
}

interface SimilarityEntry {
  concept1: string;
  concept2: string;
  similarity: string;
  forward_explanation: string;
  backward_explanation: string;
  summary_explanation: string;
  description_tree1: DescriptionTree;
  description_tree2: DescriptionTree;
}

function App() {
  const [open, setOpen] = useState(true);
  const [isLoadFolder, setIsLoadFolder] = React.useState<boolean>(false);
  const [conceptNames, setConceptNames] = useState<string[]>([]);
  const [fileNames, setFileNames] = useState<string[]>([]);
  const [listSimilarity, setListSimilarity] = useState<SimilarityEntry[]>([])
  const [mapping, setMapping] = useState<string>('');
  const [baseIRI, setBaseIRI] = useState<string>('');
  const [mappingIds, setMappingIds] = useState<string[]>([]);

  const fetchConceptNames = () => {
    findAllConceptNames()
      .then(response => setConceptNames(response.data))
      .catch(_error => {
        // console.error('Error fetching concept names:', error)
        setConceptNames([])
      });
  };

  const fetchMappingFile = () => {
    readMappingFileContent().then(response => {
      setMapping(response.data);
      readBaseIRI().then(response => {
        setBaseIRI(response.data)
      })
      fetchMappingID()
    }).catch(_error => {
      // console.error('Error reading mapping:', error)
      setMapping("");
      setBaseIRI("");
      setMappingIds([]);
    });
  }

  const fetchMappingID = () => {
    getMappingID().then(response => {
      setMappingIds(response.data);
    }).catch(_error => {
      // console.error('Error reading mapping:', error)
      setMappingIds([]);
    });
  }

  const fetchFileNames = () => {
    getOWLFilename()
      .then(response => {
        setFileNames(response.data);
      }
      )
      .catch(_error => {
        // Handle error by setting fileNames to an empty array
        setFileNames([]);
      });
  }

  const fetchSimilarityList = () => {
    readSimilarityFileContent()
      .then(response => {
        setListSimilarity(response.data);
        console.log(response.data);
      })
      .catch(_error => {
        // console.error('Error reading similarity file:', error)
        setListSimilarity([]);
      });
  }

  const setUser = (username: string) => {

    createUserFolder(username)
      .then(() => {
        console.log(`Creating folder for user: ${username}`);
        setIsLoadFolder(true);
        fetchConceptNames();
        fetchFileNames();
        fetchSimilarityList();
        fetchMappingFile();
        fetchMappingID();
      })
      .catch(error => {
        console.error('Error creating user folder:', error)
      });
  }

  const handleUploadFile = (owlFile: File, mappingFile: File, propertiesFile: File, driverFile: File, apiKey: string) => {
    if (owlFile && mappingFile && propertiesFile && driverFile) {
      const formData = new FormData();
      formData.append('owlFile', owlFile);
      formData.append('mappingFile', mappingFile);
      formData.append('propertiesFile', propertiesFile);
      formData.append('driverFile', driverFile);
      formData.append('api_key', apiKey);

      uploadFiles(formData).then(response => {
        console.log('Files uploaded successfully:', response.data);
        fetchConceptNames();
        fetchFileNames();
        fetchMappingFile();
        similarityMeasureAllConcept().then().catch((error) => {
          console.error(error);
        });
      }).catch(error => {
        console.error('Error uploading files:', error);
      });
    } else {
      alert('Please upload all required files.');
    }
  };

  const handleDeleteProfile = () => {
    deleteProfile()
      .then(() => {
        console.log(`Deleted profile`);
        setConceptNames([])
        setMapping("");
        setFileNames([])
        setListSimilarity([]);
        setMappingIds([]);
      })
  };

  const handleGenerateMapping = () => {
    generateMapping(baseIRI).then(response => {
      setMapping(response.data);
      fetchFileNames();
      fetchMappingID();
    }).catch(error => console.error('Error generating mapping:', error));
  };

  return (
    <Router>
      <CssBaseline />
      <Box sx={{
        display: 'flex',
        minHeight: '100vh',
        backgroundImage: 'url(https://wallpapers.com/images/featured/minimalist-7xpryajznty61ra3.jpg)',
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat'
      }}>
        {open && (
          <Box component="nav" sx={{ width: { md: '300px' }, minHeight: "100%", flexShrink: { md: 0 } }}>
            <Sidebar open={open} setOpen={setOpen} />
          </Box>
        )}
        <Box sx={{ flexGrow: 1 }}>
          <Header toggleSidebar={() => setOpen(!open)} open={open} setUser={setUser} />
          <Box component="main" sx={{ p: 3 }}>
            <Routes>
              <Route path="/" element={<Navigate to="/mainPage" />} />
              <Route path="/mainPage" element={
                <MainPage
                  isLoadFolder={isLoadFolder}
                  conceptNames={conceptNames}
                  handleDeleteProfile={handleDeleteProfile}
                  handleUploadFile={handleUploadFile} />
              } />
              <Route path="/mappingPage" element={
                <MappingPage
                  conceptNames={conceptNames}
                  mapping={mapping}
                  setMapping={setMapping}
                  baseIRI={baseIRI}
                  setBaseIRI={setBaseIRI}
                  handleGenerateMapping={handleGenerateMapping}
                  mappingIds={mappingIds} />
              } />
              <Route path="/queryPage" element={
                <QueryPage
                  conceptNames={conceptNames}
                  fileNames={fileNames}
                  fetchSimilarityList={fetchSimilarityList}
                  listSimilarity={listSimilarity} />
              } />
              <Route path="*" element={<Navigate to="/mainPage" />} />
            </Routes>
          </Box>
        </Box>
      </Box>
    </Router>
  );
}

export default App;
