// src/components/MappingPage.tsx
import { Grid } from '@mui/material';
import MappingSection from '../components/mappingComponents/MappingSection.tsx';
import ConceptNames from '../components/ConceptSection.tsx';
import AutoCorrectSection from '../components/mappingComponents/AutoCorrectSection.tsx';
import MappingVisualizeSection from '../components/mappingComponents/MappingVisualizeSection.tsx';

const MappingPage = ({ conceptNames, mapping, setMapping, baseIRI,
  setBaseIRI, handleGenerateMapping, mappingIds }: {
    conceptNames: string[], mapping: string, setMapping: (value: string) => void, baseIRI: string, setBaseIRI: (value: string) => void, handleGenerateMapping: () => void
    , mappingIds: string[]
  }) => {


  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={8}>
        <MappingSection mapping={mapping} setMapping={setMapping} baseIRI={baseIRI}
          setBaseIRI={setBaseIRI} handleGenerateMapping={handleGenerateMapping} />
        <MappingVisualizeSection mappingIds={mappingIds} />
      </Grid>
      <Grid item xs={12} md={4}>
        <ConceptNames conceptNames={conceptNames} />
        <AutoCorrectSection mapping={mapping} setMapping={setMapping} conceptNames={conceptNames} />
      </Grid>
    </Grid>
  );
};

export default MappingPage;
