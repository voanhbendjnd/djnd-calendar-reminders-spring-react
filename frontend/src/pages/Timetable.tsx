import React, { useEffect, useState } from 'react';
import { Button, message, DatePicker, Spin } from 'antd';
import { LeftOutlined, RightOutlined, VideoCameraOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import isoWeek from 'dayjs/plugin/isoWeek';
import api from '../services/api';

dayjs.extend(isoWeek);

const Timetable: React.FC = () => {
  const [currentDate, setCurrentDate] = useState(dayjs());
  const [loading, setLoading] = useState(false);
  const [classes, setClasses] = useState<any[]>([]);

  const fetchWeek = async (date: dayjs.Dayjs) => {
    setLoading(true);
    try {
      const { data } = await api.get(`/timetables/week?date=${date.format('YYYY-MM-DD')}`);
      setClasses(data);
    } catch (error) {
      message.error('Failed to load timetable');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWeek(currentDate);
  }, [currentDate]);

  const handlePrevWeek = () => setCurrentDate(currentDate.subtract(1, 'week'));
  const handleNextWeek = () => setCurrentDate(currentDate.add(1, 'week'));

  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => currentDate.isoWeekday(i + 1));
  const slots = [1, 2, 3, 4, 5, 6, 7, 8];

  const getClassForSlotAndDay = (slot: number, dateStr: string) => {
    return classes.find(c => c.slot === slot && c.date === dateStr);
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold m-0">Weekly Timetable</h2>
        <div className="flex gap-2 items-center">
          <Button icon={<LeftOutlined />} onClick={handlePrevWeek} />
          <DatePicker 
            value={currentDate} 
            onChange={(d) => d && setCurrentDate(d)} 
            allowClear={false}
            format="DD/MM/YYYY"
          />
          <Button icon={<RightOutlined />} onClick={handleNextWeek} />
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center p-12"><Spin size="large" /></div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full border-collapse min-w-[800px]">
            <thead>
              <tr>
                <th className="border p-2 bg-gray-50 text-center w-16">Slot</th>
                {daysOfWeek.map(d => (
                  <th key={d.format('YYYY-MM-DD')} className={`border p-2 text-center ${d.format('YYYY-MM-DD') === dayjs().format('YYYY-MM-DD') ? 'bg-blue-50 text-blue-600' : 'bg-gray-50'}`}>
                    {d.format('ddd')}<br/>
                    <span className="text-sm font-normal text-gray-500">{d.format('DD/MM')}</span>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {slots.map(slot => (
                <tr key={slot}>
                  <td className="border p-2 text-center font-bold bg-gray-50">{slot}</td>
                  {daysOfWeek.map(d => {
                    const cls = getClassForSlotAndDay(slot, d.format('YYYY-MM-DD'));
                    return (
                      <td key={d.format('YYYY-MM-DD')} className="border p-2 align-top h-24 min-w-[140px] hover:bg-gray-50 transition-colors">
                        {cls ? (
                          <div className="flex flex-col h-full text-sm">
                            <div className="font-bold text-blue-600 truncate" title={`${cls.subjectCode} - ${cls.className}`}>
                              {cls.subjectCode} - {cls.className}
                            </div>
                            <div className="text-gray-500 text-xs mt-1">
                              {cls.startTime.substring(0, 5)} - {cls.endTime.substring(0, 5)}
                            </div>
                            <div className="text-gray-600 mt-1 truncate" title={cls.room}>Room: {cls.room}</div>
                            
                            <div className="mt-auto pt-2 flex items-center justify-between">
                              <span className={`text-[10px] px-1.5 py-0.5 rounded ${cls.mode === 'ONLINE' ? 'bg-green-100 text-green-700' : 'bg-gray-200 text-gray-700'}`}>
                                {cls.mode}
                              </span>
                              {cls.mode === 'ONLINE' && cls.meetingUrl && (
                                <a href={cls.meetingUrl} target="_blank" rel="noreferrer" className="text-green-600 hover:text-green-800" title="Join Meeting">
                                  <VideoCameraOutlined className="text-lg" />
                                </a>
                              )}
                            </div>
                          </div>
                        ) : (
                          <div className="h-full flex items-center justify-center text-gray-300">-</div>
                        )}
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default Timetable;
