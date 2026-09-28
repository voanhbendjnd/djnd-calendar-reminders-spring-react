import React, { useState } from 'react';
import { Card, Input, Button, message, Typography } from 'antd';
import api from '../services/api';

const { Title, Paragraph, Text } = Typography;
const { TextArea } = Input;

const ImportTimetable: React.FC = () => {
  const [html, setHtml] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);

  const handleImport = async () => {
    if (!html.trim()) {
      message.warning('Please paste the HTML content first');
      return;
    }
    setLoading(true);
    setResult(null);
    try {
      const { data } = await api.post('/timetables/import', html, {
        headers: {
          'Content-Type': 'text/plain',
        },
      });
      message.success('Timetable imported successfully!');
      setResult(data);
    } catch (error: any) {
      message.error(error.response?.data?.message || 'Failed to import timetable');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <Title level={2}>Import Timetable</Title>
      <Card className="mb-6 shadow-sm">
        <Typography>
          <Paragraph>
            Follow these steps to import your FAP timetable:
          </Paragraph>
          <ol className="list-decimal pl-5 space-y-1 mb-4 text-gray-700">
            <li>Open FAP and navigate to your <strong>Weekly Timetable</strong>.</li>
            <li>Right click on the timetable page and click <strong>Inspect</strong> or <strong>Inspect Element</strong>.</li>
            <li>Find the element containing the weekly timetable (usually a <Text code>&lt;table&gt;</Text> or the main container <Text code>&lt;div class="table-responsive..."&gt;</Text>).</li>
            <li>Right click on the element in the inspector, select <strong>Copy &gt; Copy element</strong> (or Copy outerHTML).</li>
            <li>Paste the copied HTML into the text area below.</li>
          </ol>
        </Typography>
        
        <TextArea 
          rows={10} 
          placeholder="Paste FAP HTML here..." 
          value={html}
          onChange={(e) => setHtml(e.target.value)}
          className="mb-4 font-mono text-sm"
        />
        
        <div className="flex gap-4">
          <Button type="primary" onClick={handleImport} loading={loading} size="large">
            Import Timetable
          </Button>
          <Button onClick={() => setHtml('')} size="large">
            Clear
          </Button>
        </div>
      </Card>

      {result && (
        <Card className="shadow-sm bg-green-50 border-green-200">
          <Title level={4} className="text-green-800">Import Successful</Title>
          <ul className="list-disc pl-5 text-gray-800">
            <li><strong>Week:</strong> {result.weekStart} to {result.weekEnd}</li>
            <li><strong>Imported:</strong> {result.imported} new classes</li>
            <li><strong>Updated:</strong> {result.updated} classes</li>
            <li><strong>Skipped:</strong> {result.skipped} identical classes</li>
          </ul>
        </Card>
      )}
    </div>
  );
};

export default ImportTimetable;
