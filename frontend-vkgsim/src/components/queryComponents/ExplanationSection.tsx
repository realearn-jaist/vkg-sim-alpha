import { useState } from 'react';
import { Box, Typography, Modal, Paper, Grid, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

interface DescriptionTree {
  conceptName: string;
  primitiveConcepts: string[];
  roleName?: string;
  existentials?: DescriptionTree[];
}

interface SymmetricPair<T> {
  first: T;
  second: T;
}

interface ExplanationTree {
  comparingConcept1: string;
  comparingConcept2: string;
  deg?: number; // degree of similarity
  pri?: SymmetricPair<string>[]; // list of primitive pairs
  exi?: SymmetricPair<string>[]; // list of existential pairs
  emb?: { [key: string]: SymmetricPair<string>[] }; // map of embedding pairs with list of value pairs
  children?: ExplanationTree[]; // nested children representing subtrees
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

interface ExplanationSectionProps {
  listSimilarity: SimilarityEntry[];
}

const buildTreeAscii = (node: DescriptionTree, prefix: string = '', isTail: boolean = true): string => {
  let result = `${prefix}${isTail ? '└── ' : '├── '}${node.roleName || node.conceptName} : ${node.primitiveConcepts.join(', ')}\n`;
  if (node.existentials) {
    const children = node.existentials;
    for (let i = 0; i < children.length - 1; i++) {
      result += buildTreeAscii(children[i], prefix + (isTail ? '    ' : '│   '), false);
    }
    if (children.length > 0) {
      result += buildTreeAscii(children[children.length - 1], prefix + (isTail ? '    ' : '│   '), true);
    }
  }
  return result;
};

const buildExplanationTreeAscii = (node: ExplanationTree, prefix: string = '', isTail: boolean = true): string => {
  let result = `${prefix}${isTail ? '└── ' : '├── '}`;
  result += `[${node.comparingConcept1}] : [${node.comparingConcept2}] - deg=${node.deg}\n`;

  if (node.pri && node.pri.length > 0) {
    result += `${prefix}${isTail ? '    ' : '│   '}  pri: ${JSON.stringify(node.pri)}\n`;
  }

  if (node.exi && node.exi.length > 0) {
    result += `${prefix}${isTail ? '    ' : '│   '}  exi: ${JSON.stringify(node.exi)}\n`;
  }

  if (node.emb) {
    result += `${prefix}${isTail ? '    ' : '│   '}  emb:\n`;
    for (const [key, values] of Object.entries(node.emb)) {
      result += `${prefix}${isTail ? '    ' : '│   '}    └── ${key} : ${JSON.stringify(values)}\n`;
    }
  }

  if (node.children) {
    const children = node.children;
    for (let i = 0; i < children.length - 1; i++) {
      result += buildExplanationTreeAscii(children[i], prefix + (isTail ? '    ' : '│   '), false);
    }
    if (children.length > 0) {
      result += buildExplanationTreeAscii(children[children.length - 1], prefix + (isTail ? '    ' : '│   '), true);
    }
  }

  return result;
};

const ExplanationSection = ({ listSimilarity }: ExplanationSectionProps) => {
  const [modalOpen, setModalOpen] = useState(false);
  const [similarityDetails, setSimilarityDetails] = useState<SimilarityEntry | null>(null);

  const handleModalClose = () => setModalOpen(false);
  const handleModalOpen = (details: SimilarityEntry) => {
    setSimilarityDetails(details);
    setModalOpen(true);
  };

  return (
    <Box mb={4}>

      <Paper sx={{ height: "50vh", overflow: "auto", p: 2, bgcolor: '#ffffff', borderRadius: 2, boxShadow: 3 }}>
        <Typography variant="h6" mb={2} sx={{ textAlign: 'center' }}>Explanation Section</Typography>
        {listSimilarity.length > 0 ? (
          listSimilarity.map((entry, index) => (
            <Paper
              key={index}
              sx={{
                mb: 1,
                p: 1,
                cursor: 'pointer',
                bgcolor: '#f9f9f9',
                '&:hover': {
                  bgcolor: '#e0f7fa',
                },
              }}
              onClick={() => handleModalOpen(entry)}
            >
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <Typography variant="subtitle1">
                    {entry.concept1} - {entry.concept2}: {entry.similarity}
                  </Typography>
                </Grid>
              </Grid>
            </Paper>
          ))
        ) : (
          <Typography variant="body1" sx={{ py: 5, textAlign: 'center' }}>
            No similarities above the threshold.
          </Typography>
        )}
      </Paper>
      <Modal
        open={modalOpen}
        onClose={handleModalClose}
        sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}
      >
        <Box
          sx={{
            position: 'relative',
            p: 4,
            bgcolor: 'background.paper',
            borderRadius: 2,
            maxWidth: '90vw',
            maxHeight: '80vh',
            overflowY: 'auto',
            boxShadow: 24,
          }}
        >
          {similarityDetails && (
            <>
              <IconButton
                sx={{ position: 'absolute', top: 8, right: 8 }}
                onClick={handleModalClose}
              >
                <CloseIcon />
              </IconButton>
              <Typography variant="h6" mb={2}>Similarity Details</Typography>
              <Grid container spacing={4}>
                <Grid item xs={12} md={6}>
                  <Paper sx={{ p: 2, bgcolor: '#e3f2fd', borderRadius: 1, boxShadow: 1 }}>
                    <Typography variant="h6" color="primary">Concept 1</Typography>
                    <Typography><strong>Name:</strong> {similarityDetails.concept1}</Typography>
                    <Typography variant="h6" mt={2}>Description Tree 1:</Typography>
                    <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>{buildTreeAscii(similarityDetails.description_tree1)}</pre>
                  </Paper>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Paper sx={{ p: 2, bgcolor: '#f3e5f5', borderRadius: 1, boxShadow: 1 }}>
                    <Typography variant="h6" color="secondary">Concept 2</Typography>
                    <Typography><strong>Name:</strong> {similarityDetails.concept2}</Typography>
                    <Typography variant="h6" mt={2}>Description Tree 2:</Typography>
                    <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>{buildTreeAscii(similarityDetails.description_tree2)}</pre>
                  </Paper>
                </Grid>
              </Grid>
              <Box mt={2} sx={{ p: 2, bgcolor: '#fafafa', borderRadius: 1, boxShadow: 1 }}>
                {similarityDetails.summary_explanation ? (
                  <>
                    <Typography variant="h6" mt={2}>Summary Explanation:</Typography>
                    <Typography>{similarityDetails.summary_explanation}</Typography>
                    <Typography variant="h6" mt={2}>Forward Explanation:</Typography>
                    <Typography>{similarityDetails.forward_explanation}</Typography>
                    <Typography variant="h6" mt={2}>Backward Explanation:</Typography>
                    <Typography>{similarityDetails.backward_explanation}</Typography>
                  </>
                ) : (
                  <>
                    <Typography variant="h6" mt={2}>Forward Explanation Tree:</Typography>
                    <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>{buildExplanationTreeAscii(JSON.parse(similarityDetails.forward_explanation))}</pre>
                    <Typography variant="h6" mt={2}>Backward Explanation Tree:</Typography>
                    <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>{buildExplanationTreeAscii(JSON.parse(similarityDetails.backward_explanation))}</pre>
                  </>
                )}
              </Box>
            </>
          )}
        </Box>
      </Modal>
    </Box>
  );
};

export default ExplanationSection;
