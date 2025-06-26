// src/components/MainPage.tsx
import { Grid } from '@mui/material';
import UploadForm from '../components/mainComponents/UploadForm.tsx';
import ConceptNames from '../components/ConceptSection.tsx';



const MainPage = ({ isLoadFolder, conceptNames, handleDeleteProfile, handleUploadFile}: { isLoadFolder: boolean, 
  conceptNames: string[], handleDeleteProfile: () => void, handleUploadFile: (owlFile: File, mappingFile: File, propertiesFile: File, driverFile: File, apiKey: string) => void}) => {
  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={8}>
        <UploadForm isLoadFolder={isLoadFolder} handleDeleteProfile={handleDeleteProfile} handleUploadFile={handleUploadFile}/>
      </Grid>
      <Grid item xs={12} md={4}>
        <ConceptNames conceptNames={conceptNames} />
      </Grid>
    </Grid>
  );
};

export default MainPage;
