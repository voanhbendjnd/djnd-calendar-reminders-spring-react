import React, { useEffect, useState } from 'react';
import { Card, Spin, Button, message } from 'antd';
import { ClockCircleOutlined, BookOutlined, UserOutlined } from '@ant-design/icons';
import api from '../services/api';

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [classes, setClasses] = useState<any[]>([]);

  useEffect(() => {
    const fetchToday = async () => {
      try {
        const today = new Date().toISOString().split('T')[0];
        const { data } = await api.get(`/timetables/day?date=${today}`);
        setClasses(data);
      } catch (error) {
        message.error('Failed to load today classes');
      } finally {
        setLoading(false);
      }
    };
    fetchToday();
  }, []);

  if (loading) return <div className="flex justify-center p-12"><Spin size="large" /></div>;

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Today's Classes</h2>
      {classes.length === 0 ? (
        <Card className="text-center bg-gray-50 border-dashed">
          <p className="text-gray-500 mb-0 text-lg">You have no classes today. Relax!</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {classes.map((cls, idx) => (
            <Card key={idx} className="shadow-sm border-l-4 border-l-blue-500 hover:shadow-md transition-shadow">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="text-lg font-bold m-0 text-blue-600">{cls.subjectCode}</h3>
                  <span className="text-gray-500">{cls.className}</span>
                </div>
                <div className="text-right">
                  <div className="font-semibold text-gray-700">
                    <ClockCircleOutlined className="mr-1" />
                    {cls.startTime.substring(0, 5)} - {cls.endTime.substring(0, 5)}
                  </div>
                  <div className={`text-xs font-semibold px-2 py-1 rounded-full mt-1 inline-block ${cls.mode === 'ONLINE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'}`}>
                    {cls.mode}
                  </div>
                </div>
              </div>
              <div className="text-sm text-gray-600 space-y-1">
                <p className="m-0"><BookOutlined className="mr-2" /> Room: {cls.room || 'N/A'}</p>
                <p className="m-0"><UserOutlined className="mr-2" /> Lecturer: {cls.lecturer || 'N/A'}</p>
              </div>
              {cls.mode === 'ONLINE' && cls.meetingUrl && (
                <div className="mt-4">
                  <a href={cls.meetingUrl} target="_blank" rel="noreferrer">
                    <Button type="primary" className="bg-green-600 w-full">Join Meeting</Button>
                  </a>
                </div>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
