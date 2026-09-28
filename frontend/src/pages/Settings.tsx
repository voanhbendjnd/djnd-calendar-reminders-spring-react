import React from 'react';
import { Card, Switch, Form, TimePicker, Button, message, Typography } from 'antd';

const { Title, Paragraph } = Typography;

const Settings: React.FC = () => {
  const onFinish = (_values: any) => {
    message.success('Settings saved successfully!');
    // Ideally this would call the backend to update user_notification_settings
  };

  return (
    <div className="max-w-2xl mx-auto">
      <Title level={2}>Settings</Title>
      <Card className="shadow-sm">
        <Title level={4}>Daily Email Notifications</Title>
        <Paragraph className="text-gray-600 mb-6">
          Receive a daily summary of your classes via email every morning.
        </Paragraph>

        <Form layout="vertical" onFinish={onFinish} initialValues={{ enabled: true }}>
          <Form.Item name="enabled" label="Enable daily notifications" valuePropName="checked">
            <Switch />
          </Form.Item>
          
          <Form.Item label="Notification Time" help="Emails are currently fixed to send at 05:00 AM (Vietnam Time)">
            <TimePicker disabled value={null} placeholder="05:00" format="HH:mm" />
          </Form.Item>
          
          <Form.Item>
            <Button type="primary" htmlType="submit">
              Save Settings
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Settings;
