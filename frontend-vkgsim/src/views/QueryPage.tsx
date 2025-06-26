// QueryPage.tsx
import { Grid } from '@mui/material';
import ConceptSection from '../components/ConceptSection';
import ExplanationSection from '../components/queryComponents/ExplanationSection';
import QuerySection from '../components/queryComponents/QuerySection';
import { useEffect, useState } from 'react';
import { saveSimResultFile } from '../api';

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


const QueryPage = ({ conceptNames, fileNames, fetchSimilarityList, listSimilarity }: { conceptNames: string[], fileNames: string[], fetchSimilarityList: () => void, listSimilarity: SimilarityEntry[] }) => {
  const [queryType, setQueryType] = useState<'standard' | 'similarity'>('standard');
  const [similarityThreshold, setSimilarityThreshold] = useState<number>(0.5);
  const [result, setResult] = useState<string>('');

  const handleQueryTypeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newQueryType = event.target.value as 'standard' | 'similarity';
    setQueryType(newQueryType);
  };

  // useEffect to calculate and update `result` when `listSimilarity` or `similarityThreshold` changes
  useEffect(() => {
    if (listSimilarity.length > 0) {
      const updatedResult = listSimilarity
        .filter(entry => parseFloat(entry.similarity) > similarityThreshold)
        .map(entry => `${entry.concept1},${entry.concept2}`)
        .join('\n');

      setResult(updatedResult);
    } else {
      setResult('');
    }
  }, [listSimilarity, similarityThreshold]);

  // useEffect to save `result` when it has content
  useEffect(() => {
    if (result) {
      saveSimResultFile(result);
    }
  }, [result]);

  const filteredSimilarities = listSimilarity
    .filter(entry => parseFloat(entry.similarity) > similarityThreshold)
    .sort((a, b) => parseFloat(b.similarity) - parseFloat(a.similarity));


  useEffect(() => {
    fetchSimilarityList();
  }, [])

  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={8}>
        <QuerySection fileNames={fileNames} queryType={queryType} handleQueryTypeChange={handleQueryTypeChange} similarityThreshold={similarityThreshold} setSimilarityThreshold={setSimilarityThreshold} />
      </Grid>
      <Grid item xs={12} md={4}>
        <ConceptSection conceptNames={conceptNames} />
        {queryType === 'similarity' ? <ExplanationSection listSimilarity={filteredSimilarities} /> : <></>}
      </Grid>
    </Grid>
  );
};

export default QueryPage;
